import java.io.*;
import java.util.*;

public class ProblemaP2 {

    static class Estado {
        int pos;            
        int energia;        
        List<String> acciones;
        int pasos;          

        Estado(int pos, int energia, List<String> acciones, int pasos) {
            this.pos = pos;
            this.energia = energia;
            this.acciones = acciones;
            this.pasos = pasos;
        }
    }

    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String linea = br.readLine();
        if (linea == null) return;
        int numero_casos = Integer.parseInt(linea.trim());

        for (int caso = 0; caso < numero_casos; caso++) {

            do { linea = br.readLine(); }
            while (linea != null && linea.trim().isEmpty());
            if (linea == null) break;
            String[] ne = linea.trim().split("\\s+");
            int n = Integer.parseInt(ne[0]);     // Numero de plataformas
            int e = Integer.parseInt(ne[1]);     // Energia inicial

            // Leer plataformas con robots
            
            linea = br.readLine();
            Set<Integer> robots = new HashSet<>();
            if (linea != null && !linea.trim().isEmpty()) {
                for (String s : linea.trim().split("\\s+"))
                    robots.add(Integer.parseInt(s));
            }

            // Leer poderes de salto (Plataforma/Salto)
            
            linea = br.readLine();
            Map<Integer,Integer> poderes = new HashMap<>();
            if (linea != null && !linea.trim().isEmpty()) {
                String[] ps = linea.trim().split("\\s+");
                for (int i = 0; i + 1 < ps.length; i += 2) {
                    int pi = Integer.parseInt(ps[i]);
                    int si = Integer.parseInt(ps[i+1]);
                    poderes.put(pi, si);
                }
            }

            System.out.println(bfs(n, e, robots, poderes));    // Llamar a la funcion BFS
        }

        br.close();
    }

    private static String bfs(int n, int energiaInicial, Set<Integer> robots,Map<Integer,Integer> poderes) {

        int objetivo = n;    // La plataforma objetivo 
        
        Map<String, Integer> visitado = new HashMap<>();   // Visitados: (pos, energía) para no repetir
        
        Deque<Estado> q = new ArrayDeque<>();
        q.addLast(new Estado(0, energiaInicial, new ArrayList<>(), 0));

        while (!q.isEmpty()) {


            Estado cur = q.removeFirst();

            if (cur.pos == objetivo) {
                StringBuilder sb = new StringBuilder();
                sb.append(cur.pasos);
                for (String accion : cur.acciones) {
                    sb.append(" ").append(accion);
                }
                return sb.toString();
            }

            
            String clave = cur.pos + "," + cur.energia;
            if (visitado.containsKey(clave) && visitado.get(clave) <= cur.pasos) continue;
            visitado.put(clave, cur.pasos);


            // Caminar adelante/atrás

            for (int dir : new int[]{1, -1}) {     // 1: Adelante y -1: Atras
                int np = cur.pos + dir;
                if (np >= 1 && np <= n && !robots.contains(np)) {
                    List<String> na = new ArrayList<>(cur.acciones);
                    na.add(dir == 1 ? "C+" : "C-");
                    q.addLast(new Estado(np, cur.energia, na, cur.pasos + 1));
                }
            }

            // Saltar si hay poder en la plataforma actual

            if (poderes.containsKey(cur.pos)) {
                int salto = poderes.get(cur.pos);
                for (int dir : new int[]{1, -1}) {
                    int np = cur.pos + (dir * salto);
                    if (np >= 1 && np <= n && !robots.contains(np)) {
                        List<String> na = new ArrayList<>(cur.acciones);
                        na.add(dir == 1 ? "S+" : "S-");
                        q.addLast(new Estado(np, cur.energia, na, cur.pasos + 1));
                    }
                }
            }

            // Teletransportarse 

            for (int dest = 1; dest <= n; dest++) {
                if (dest == cur.pos || robots.contains(dest)) continue;
                int coste = Math.abs(dest - cur.pos);
                if (coste <= cur.energia && cur.energia > 0) {
                    List<String> na = new ArrayList<>(cur.acciones);
                    int diff = dest - cur.pos;
                    na.add("T" + String.valueOf(diff));
                    q.addLast(new Estado(dest, cur.energia - coste, na, cur.pasos + 1));
                }
            }
        }

        return "NO SE PUEDE"; // Fin de la cola
    }
}