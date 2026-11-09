package ui;

import domain.Friendship;
import domain.User;
import repository.CriteriiUtilizator;
import service.CommunityService;
import service.FriendshipService;
import service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * clasa responsabila cu interfata utilizator
 */
public class Console {
    private UserService srvUtilizator;
    private FriendshipService srvPrietenie;
    private CommunityService srvComunitate;
    private Scanner scanner;

    /**
     * constructor pentru consola
     * preia serviceurile necesare
     *
     * @param srvUtilizator serviceul utilizatorilor
     * @param srvPrietenie serviceul prieteniilor
     * @param srvComunitate serviceul pentru analiza comunitatiilor(nr lor si cea mai sociabila)
     */
    public Console(UserService srvUtilizator, FriendshipService srvPrietenie, CommunityService srvComunitate) {
        this.srvUtilizator = srvUtilizator;
        this.srvPrietenie = srvPrietenie;
        this.srvComunitate = srvComunitate;
        this.scanner = new Scanner(System.in);
    }

    /**
     * functie ce printeaza meniul aplicatiei
     */
    private void printMenu() {
        System.out.println("\n Meniu Retea Socializare");
        System.out.println("1. Adauga utilizator");
        System.out.println("2. Sterge utilizator");
        System.out.println("3. Afiseaza toti utilizatorii");
        System.out.println("4. Adauga prietenie");
        System.out.println("5. Sterge prietenie");
        System.out.println("6. Afiseaza toate prieteniile");
        System.out.println("7. Afiseaza numarul de comunitati");
        System.out.println("8. Afiseaza cea mai sociabila comunitate");
        System.out.println("9. Filtrare utilizatori dupa criterii");
        System.out.println("0. Iesire");
    }
    private void uiFilterUsers() {
        System.out.println("\n Meniu Filtrare Utilizatori");
        System.out.println("1. Filtrează după nume exact");
        System.out.println("2. Filtrează după vârstă minimă");
        System.out.println("3. Utilizatori majori ale căror nume nu încep cu o anumită literă");
        System.out.print("Alegeți opțiunea de filtrare: ");

        String opt = scanner.nextLine();
        Predicate<User> filtru;

        switch (opt) {
            case "1":
                System.out.print("Introduceți numele: ");
                String nume = scanner.nextLine();
                filtru = CriteriiUtilizator.areNumele(nume);
                break;

            case "2":
                System.out.print("Introduceți vârsta minimă: ");
                int varsta = Integer.parseInt(scanner.nextLine());
                filtru = CriteriiUtilizator.areVarstaMinima(varsta);
                break;

            case "3":
                System.out.print("Introduceți litera cu care NU trebuie să înceapă numele: ");
                String litera = scanner.nextLine();

                filtru = ((Predicate<User>) CriteriiUtilizator::esteMajor)
                        .and(Predicate.not(CriteriiUtilizator.numeIncepeCu(litera)));
                break;

            default:
                System.out.println("Opțiune invalidă!");
                return;
        }

        List<User> rezultate = srvUtilizator.filterUsers(filtru);

        if (rezultate.isEmpty()) {
            System.out.println("Nu s-a găsit niciun utilizator conform criteriilor.");
        } else {
            System.out.println("\nUtilizatori găsiți:");
            rezultate.forEach(System.out::println);
        }
    }
    /**
     * porneste bucla principala, citeste comenziile si ruleaza comanda si verifica daca e valida(0-8)
     */
    public void run() {
        while (true) {
            printMenu();
            System.out.print("Alege o optiune: ");
            String optiune = scanner.nextLine();

            try {
                switch (optiune) {
                    case "1":
                        System.out.print("Nume: ");
                        String firstName = scanner.nextLine();
                        System.out.print("Prenume: ");
                        String lastName = scanner.nextLine();
                        System.out.print("Varsta: ");
                        String varsta = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.nextLine();
                        System.out.print("Parola: ");
                        String parola = scanner.nextLine();
                        srvUtilizator.addUser(firstName, lastName, Integer.parseInt(varsta), email, parola);
                        System.out.println("Utilizator adaugat cu succes!");
                        break;

                    case "2":
                        System.out.print("Introdu ID-ul utilizatorului de sters: ");
                        Integer idSters = Integer.parseInt(scanner.nextLine());
                        srvPrietenie.removeAllFriendshipsOfUser(idSters);
                        srvUtilizator.deleteUser(idSters);
                        System.out.println("Utilizator sters!");
                        break;

                    case "3":
                        System.out.println("\nLista Utilizatori");
                        for (User u : srvUtilizator.getAllUsers()) {
                            System.out.println(u);
                        }
                        break;

                    case "4":
                        System.out.print("ID Utilizator 1: ");
                        Integer id1 = Integer.parseInt(scanner.nextLine());
                        System.out.print("ID Utilizator 2: ");
                        Integer id2 = Integer.parseInt(scanner.nextLine());

                        srvPrietenie.addFriendship(id1, id2);
                        System.out.println("Prietenie creata cu succes!");
                        break;

                    case "5":
                        System.out.print("ID Utilizator 1: ");
                        Integer idSterg1 = Integer.parseInt(scanner.nextLine());
                        System.out.print("ID Utilizator 2: ");
                        Integer idSterg2 = Integer.parseInt(scanner.nextLine());

                        srvPrietenie.deleteFriendship(idSterg1, idSterg2);
                        System.out.println("Prietenie stearsa!");
                        break;

                    case "6":
                        System.out.println("\nLista Prietenii");
                        for (Friendship f : srvPrietenie.getAllFriendships()) {
                            System.out.println(f);
                        }
                        break;

                    case "7":
                        int nr = srvComunitate.numarDeComunitati();
                        System.out.println("\nNumarul total de comunitati este: " + nr);
                        break;

                    case "8":
                        List<Integer> sociabili = srvComunitate.getMostSociableCommunity();
                        System.out.println("\nCea mai sociabila comunitate este formata din utilizatorii cu ID-urile: " + sociabili);
                        break;
                    case "9":
                        uiFilterUsers();
                        break;
                    case "0":
                        System.out.println("Iesire program");
                        return;

                    default:
                        System.out.println("Optiune invalida! Incearca din nou.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Eroare: Te rog sa introduci un numar valid pentru ID!");
            } catch (Exception e) {
                System.out.println("Eroare: " + e.getMessage());
            }
        }
    }
}