package service;

import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.Repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Service pentru analiza comunităților (componente conexe și diametru de graf).
 */
public class CommunityService {
    private final Repository<Integer, User> UserRepository;
    private final Repository<Tuple<Integer, Integer>, Friendship> FriendshipRepository;

    public CommunityService(Repository<Integer, User> userRepository, Repository<Tuple<Integer, Integer>, Friendship> friendshipRepository) {
        this.UserRepository = userRepository;
        this.FriendshipRepository = friendshipRepository;
    }

    /**
     * Metodă ajutătoare pentru conversia Iterable -> Stream
     */
    private <T> Stream<T> toStream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Construiește lista de adiacență a rețelei folosind Stream API.
     */
    public Map<Integer, List<Integer>> construiesteGraf() {
        Map<Integer, List<Integer>> comunitate = toStream(UserRepository.findAll())
                .collect(Collectors.toMap(User::getId, u -> new ArrayList<>()));

        toStream(FriendshipRepository.findAll()).forEach(f -> {
            comunitate.get(f.getId().getVal1()).add(f.getId().getVal2());
            comunitate.get(f.getId().getVal2()).add(f.getId().getVal1());
        });

        return comunitate;
    }

    private void DFS(Integer nod, Map<Integer, List<Integer>> graf, Set<Integer> vizitati) {
        vizitati.add(nod);
        graf.get(nod).stream()
                .filter(vecin -> !vizitati.contains(vecin))
                .forEach(vecin -> DFS(vecin, graf, vizitati));
    }

    /**
     * Calculează numărul de componente conexe din graf.
     */
    public Integer numarDeComunitati() {
        Map<Integer, List<Integer>> graf = construiesteGraf();
        Set<Integer> vizitati = new HashSet<>();

        return (int) graf.keySet().stream()
                .filter(id -> !vizitati.contains(id))
                .peek(id -> DFS(id, graf, vizitati))
                .count();
    }

    private void DFS_Comunitate(Integer nod, Map<Integer, List<Integer>> graf, Set<Integer> vizitati, List<Integer> comunitate) {
        comunitate.add(nod);
        vizitati.add(nod);
        graf.get(nod).stream()
                .filter(vecin -> !vizitati.contains(vecin))
                .forEach(vecin -> DFS_Comunitate(vecin, graf, vizitati, comunitate));
    }

    private List<List<Integer>> extrageToateComunitatile() {
        Map<Integer, List<Integer>> graf = construiesteGraf();
        Set<Integer> vizitati = new HashSet<>();

        return graf.keySet().stream()
                .filter(id -> !vizitati.contains(id))
                .map(id -> {
                    List<Integer> comunitateCurenta = new ArrayList<>();
                    DFS_Comunitate(id, graf, vizitati, comunitateCurenta);
                    return comunitateCurenta;
                })
                .collect(Collectors.toList());
    }

    private int celMaiLungDrum(Integer nod, Map<Integer, List<Integer>> graf) {
        Queue<Integer> coada = new LinkedList<>();
        Map<Integer, Integer> distante = new HashMap<>();
        coada.add(nod);
        distante.put(nod, 0);
        int maxDist = 0;

        while (!coada.isEmpty()) {
            Integer curent = coada.poll();
            int distCurenta = distante.get(curent);
            maxDist = Math.max(maxDist, distCurenta);

            graf.get(curent).stream()
                    .filter(vecin -> !distante.containsKey(vecin))
                    .forEach(vecin -> {
                        distante.put(vecin, distCurenta + 1);
                        coada.add(vecin);
                    });
        }
        return maxDist;
    }

    /**
     * Determină comunitatea cu cel mai lung drum (diametrul maxim al unei componente).
     */
    public List<Integer> getMostSociableCommunity() {
        Map<Integer, List<Integer>> graf = construiesteGraf();
        List<List<Integer>> toateComunitatile = extrageToateComunitatile();

        return toateComunitatile.stream()
                .max(Comparator.comparingInt(comunitate ->
                        comunitate.stream()
                                .mapToInt(membru -> celMaiLungDrum(membru, graf))
                                .max()
                                .orElse(0)
                ))
                .orElseGet(ArrayList::new);
    }
}