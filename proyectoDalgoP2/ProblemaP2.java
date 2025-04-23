import java.util.*;

public class ProblemaP2 {

    static class Estado implements Comparable<Estado> {
        int posicion;
        int energia;
        int acciones;
        List<String> ruta;

        Estado(int posicion, int energia, int acciones, List<String> ruta) {
            this.posicion = posicion;
            this.energia = energia;
            this.acciones = acciones;
            this.ruta = ruta;
        }

        @Override
        public int compareTo(Estado otro) {
            return Integer.compare(this.acciones, otro.acciones);
        }

        @Override
        public int hashCode() {
            return Objects.hash(posicion, energia);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Estado)) return false;
            Estado o = (Estado) obj;
            return this.posicion == o.posicion && this.energia == o.energia;
        }
    }

    public static String resolverCaso(int n, int e, Set<Integer> robots, Map<Integer, Integer> poderes) {
        PriorityQueue<Estado> cola = new PriorityQueue<>();
        Set<String> visitados = new HashSet<>();

        cola.add(new Estado(0, e, 0, new ArrayList<>()));

        while (!cola.isEmpty()) {
            Estado actual = cola.poll();
            int pos = actual.posicion;
            int energia = actual.energia;
            int acciones = actual.acciones;
            List<String> ruta = actual.ruta;

            if (pos == n) {
                return acciones + " " + String.join(" ", ruta);
            }

            String claveEstado = pos + "-" + energia;
            if (visitados.contains(claveEstado)) continue;
            visitados.add(claveEstado);

            // Caminar (adelante y atrás)
            for (int d = -1; d <= 1; d += 2) {
                int nuevo = pos + d;
                if (nuevo >= 0 && nuevo <= n && !robots.contains(nuevo)) {
                    List<String> nuevaRuta = new ArrayList<>(ruta);
                    nuevaRuta.add(d == 1 ? "C+" : "C-");
                    cola.add(new Estado(nuevo, energia, acciones + 1, nuevaRuta));
                }
            }

            // Saltar con poder
            if (poderes.containsKey(pos)) {
                int salto = poderes.get(pos);
                for (int d = -1; d <= 1; d += 2) {
                    int nuevo = pos + d * salto;
                    if (nuevo >= 0 && nuevo <= n && !robots.contains(nuevo)) {
                        List<String> nuevaRuta = new ArrayList<>(ruta);
                        nuevaRuta.add(d == 1 ? "S+" : "S-");
                        cola.add(new Estado(nuevo, energia, acciones + 1, nuevaRuta));
                    }
                }
            }

            // Teletransportarse
            for (int destino = 0; destino <= n; destino++) {
                if (destino == pos || robots.contains(destino)) continue;
                int costo = Math.abs(destino - pos) + 1;
                if (energia >= costo) {
                    List<String> nuevaRuta = new ArrayList<>(ruta);
                    int delta = destino - pos;
                    nuevaRuta.add("T" + delta);
                    cola.add(new Estado(destino, energia - costo, acciones + 1, nuevaRuta));
                }
            }
        }

        return "NO SE PUEDE";
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int casos = Integer.parseInt(sc.nextLine());

        for (int c = 0; c < casos; c++) {
            String[] linea1 = sc.nextLine().split(" ");
            int n = Integer.parseInt(linea1[0]);
            int e = Integer.parseInt(linea1[1]);

            Set<Integer> robots = new HashSet<>();
            String[] linea2 = sc.nextLine().split(" ");
            for (String s : linea2) {
                if (!s.isEmpty()) robots.add(Integer.parseInt(s));
            }

            Map<Integer, Integer> poderes = new HashMap<>();
            String[] linea3 = sc.nextLine().split(" ");
            for (int i = 0; i < linea3.length; i += 2) {
                int pos = Integer.parseInt(linea3[i]);
                int salto = Integer.parseInt(linea3[i + 1]);
                poderes.put(pos, salto);
            }

            System.out.println(resolverCaso(n, e, robots, poderes));
        }

        sc.close();
    }
}
