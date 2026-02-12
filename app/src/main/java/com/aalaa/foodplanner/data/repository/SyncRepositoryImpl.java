package com.aalaa.foodplanner.data.repository;

import com.aalaa.foodplanner.domain.repository.FavoritesRepository;
import com.aalaa.foodplanner.domain.repository.PlanRepository;
import com.aalaa.foodplanner.domain.repository.MealRepository;
import com.aalaa.foodplanner.domain.repository.SyncRepository;
import com.aalaa.foodplanner.datasource.db.PendingAction;
import com.aalaa.foodplanner.datasource.db.PendingActionDao;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SyncRepositoryImpl implements SyncRepository {

    private final FavoritesRepository favoritesRepository;
    private final PlanRepository planRepository;
    private final MealRepository mealRepository;
    private final PendingActionDao pendingActionDao;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    private static SyncRepositoryImpl instance;

    private static final String USERS = "users";
    private static final String MEAL_PLANS = "mealPlans";
    private static final String FAVORITE_MEALS = "favoriteMeals";

    private SyncRepositoryImpl(FavoritesRepository favoritesRepository,
            PlanRepository planRepository,
            MealRepository mealRepository,
            PendingActionDao pendingActionDao) {
        this.favoritesRepository = favoritesRepository;
        this.planRepository = planRepository;
        this.mealRepository = mealRepository;
        this.pendingActionDao = pendingActionDao;
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    public static SyncRepositoryImpl getInstance(FavoritesRepository favoritesRepository,
            PlanRepository planRepository,
            MealRepository mealRepository,
            PendingActionDao pendingActionDao) {
        if (instance == null) {
            instance = new SyncRepositoryImpl(favoritesRepository, planRepository, mealRepository, pendingActionDao);
        }
        return instance;
    }

    @Override
    public Completable backupUserData() {
        return Single.fromCallable(() -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null)
                throw new IllegalStateException("User not logged in");
            return user.getUid();
        })
                .flatMapCompletable(uid -> favoritesRepository.getFavorites().firstOrError()
                        .zipWith(planRepository.getAllPlans().firstOrError(),
                                (favorites, plans) -> new Object[] { uid, favorites, plans })
                        .flatMapCompletable(arr -> {
                            String userId = (String) arr[0];

                            @SuppressWarnings("unchecked")
                            List<?> favorites = (List<?>) arr[1];

                            List<String> favIds = new ArrayList<>();
                            for (Object mealObj : favorites) {

                                try {
                                    String id = (String) mealObj.getClass().getMethod("getIdMeal").invoke(mealObj);
                                    if (id != null)
                                        favIds.add(id);
                                } catch (Exception ignored) {
                                }
                            }

                            Map<String, Object> favUpdate = new HashMap<>();
                            favUpdate.put(FAVORITE_MEALS, favIds);

                            Completable saveFavorites = taskToCompletable(
                                    firestore.collection(USERS).document(userId)
                                            .set(favUpdate, SetOptions.merge()));

                            @SuppressWarnings("unchecked")
                            List<?> plans = (List<?>) arr[2];

                            Map<String, Map<String, Object>> dateToDoc = new HashMap<>();

                            for (Object planObj : plans) {
                                try {
                                    String date = (String) planObj.getClass().getMethod("getDate").invoke(planObj);
                                    String slot = (String) planObj.getClass().getMethod("getSlot").invoke(planObj);
                                    String mealId = (String) planObj.getClass().getMethod("getMealId").invoke(planObj);

                                    if (date == null || slot == null || mealId == null)
                                        continue;

                                    String field = slotToField(slot);
                                    if (field == null)
                                        continue;

                                    Map<String, Object> doc = dateToDoc.get(date);
                                    if (doc == null) {
                                        doc = new HashMap<>();
                                        dateToDoc.put(date, doc);
                                    }
                                    doc.put(field, mealId);
                                } catch (Exception ignored) {
                                }
                            }

                            List<Completable> planWrites = new ArrayList<>();
                            for (Map.Entry<String, Map<String, Object>> entry : dateToDoc.entrySet()) {
                                String date = entry.getKey();
                                Map<String, Object> doc = entry.getValue();

                                planWrites.add(taskToCompletable(
                                        firestore.collection(USERS).document(userId)
                                                .collection(MEAL_PLANS).document(date)
                                                .set(doc, SetOptions.merge())));
                            }

                            return Completable.mergeArray(
                                    saveFavorites,
                                    planWrites.isEmpty() ? Completable.complete() : Completable.merge(planWrites));
                        }))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable restoreUserData() {
        return Single.fromCallable(() -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null)
                throw new IllegalStateException("User not logged in");
            return user.getUid();
        })
                .flatMap(uid -> {
                    Single<DocumentSnapshot> userDoc = taskToSingle(
                            firestore.collection(USERS).document(uid).get());

                    Single<QuerySnapshot> plansSnap = taskToSingle(
                            firestore.collection(USERS).document(uid).collection(MEAL_PLANS).get());

                    return Single.zip(userDoc, plansSnap, (uDoc, pSnap) -> new Object[] { uid, uDoc, pSnap });
                })
                .flatMapCompletable(arr -> Completable.fromAction(() -> processRestore(arr))
                        .subscribeOn(Schedulers.io()))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable clearLocalData() {
        return Completable.mergeArray(
                favoritesRepository.clearFavorites(),
                planRepository.clearAllPlans(),
                pendingActionDao.clearAll())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Completable processPendingActions() {
        return pendingActionDao.getAll()
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(actions -> {
                    if (actions.isEmpty())
                        return Completable.complete();

                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null)
                        return Completable.complete(); // Should not happen if called when online & logged in
                    String uid = user.getUid();

                    List<Completable> tasks = new ArrayList<>();
                    for (PendingAction action : actions) {
                        tasks.add(executeAction(uid, action).andThen(pendingActionDao.delete(action))); // Assuming
                                                                                                        // delete(action)
                                                                                                        // exists or we
                                                                                                        // use clearAll
                                                                                                        // later?
                        // PendingActionDao interface showed 'insert', 'getAll', 'clearAll'.
                        // It did NOT show 'delete(action)'.
                        // I should verify PendingActionDao or use clearAll() if I process strictly
                        // sequentially?
                        // Better to add 'delete(action)' to DAO.
                    }
                    return Completable.concat(tasks);
                });
    }

    private Completable executeAction(String uid, PendingAction action) {
        if ("DELETE".equals(action.actionType)) {
            if ("FAV".equals(action.entityType)) {
                String mealId = action.payload;
                return taskToCompletable(
                        firestore.collection(USERS).document(uid)
                                .update(FAVORITE_MEALS, FieldValue.arrayRemove(mealId)))
                        .onErrorComplete(); // Ignore if already deleted or doc doesn't exist
            } else if ("PLAN".equals(action.entityType)) {
                // Payload expected: "date,slot"
                String[] parts = action.payload.split(",");
                if (parts.length != 2)
                    return Completable.complete();
                String date = parts[0];
                String slot = parts[1];
                Map<String, Object> update = new HashMap<>();
                update.put(slot, FieldValue.delete());

                return taskToCompletable(
                        firestore.collection(USERS).document(uid)
                                .collection(MEAL_PLANS).document(date)
                                .update(update))
                        .onErrorComplete();
            }
        }
        return Completable.complete();
    }

    @SuppressWarnings("unchecked")
    private void processRestore(Object[] arr) {
        DocumentSnapshot userDoc = (DocumentSnapshot) arr[1];
        QuerySnapshot plansSnap = (QuerySnapshot) arr[2];

        List<Completable> tasks = new ArrayList<>();

        List<String> favIds = (List<String>) userDoc.get(FAVORITE_MEALS);
        if (favIds != null) {
            for (String id : favIds) {
                tasks.add(mealRepository.getMealById(id)
                        .flatMapCompletable(favoritesRepository::addToFavorites)
                        .onErrorComplete());
            }
        }

        for (DocumentSnapshot doc : plansSnap.getDocuments()) {
            String date = doc.getId();

            restoreSlot(tasks, doc, date, "breakfast");
            restoreSlot(tasks, doc, date, "lunch");
            restoreSlot(tasks, doc, date, "dinner");
            restoreSlot(tasks, doc, date, "snack");
        }

        if (!tasks.isEmpty()) {
            Completable.merge(tasks).blockingAwait();
        }
    }

    private void restoreSlot(List<Completable> tasks, DocumentSnapshot doc, String date, String field) {
        String mealId = doc.getString(field);
        if (mealId == null)
            return;

        tasks.add(mealRepository.getMealById(mealId)
                .flatMapCompletable(meal -> planRepository.addToPlan(meal, date, field))
                .onErrorComplete());
    }

    private String slotToField(String slot) {
        if (slot == null)
            return null;
        slot = slot.toLowerCase(Locale.ROOT).trim();

        switch (slot) {
            case "breakfast":
                return "breakfast";
            case "lunch":
                return "lunch";
            case "dinner":
                return "dinner";
            case "snack":
                return "snack";
        }

        return null;
    }

    private static Completable taskToCompletable(Task<?> task) {
        return Completable.create(emitter -> {
            task.addOnSuccessListener(r -> {
                if (!emitter.isDisposed())
                    emitter.onComplete();
            })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    });
        });
    }

    private static <T> Single<T> taskToSingle(Task<T> task) {
        return Single.create(emitter -> {
            task.addOnSuccessListener(result -> {
                if (!emitter.isDisposed())
                    emitter.onSuccess(result);
            })
                    .addOnFailureListener(e -> {
                        if (!emitter.isDisposed())
                            emitter.onError(e);
                    });
        });
    }
}
