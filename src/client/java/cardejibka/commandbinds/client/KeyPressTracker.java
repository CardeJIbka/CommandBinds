package cardejibka.commandbinds.client;

import java.util.HashSet;
import java.util.Set;

final class KeyPressTracker {

    private static final Set<Integer> held = new HashSet<>();

    private KeyPressTracker() {}

    static boolean risingEdge(int keyCode) {
        return held.add(keyCode);
    }

    static void release(int keyCode) {
        held.remove(keyCode);
    }
}
