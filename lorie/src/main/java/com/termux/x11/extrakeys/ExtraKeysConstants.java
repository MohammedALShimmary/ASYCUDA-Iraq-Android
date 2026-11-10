package com.termux.x11.extrakeys;

import android.view.KeyEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtraKeysConstants {

    /** Defines the repetitive keys that can be passed to {@link ExtraKeysView#setRepetitiveKeys(List)}. */
    public static List<String> PRIMARY_REPETITIVE_KEYS = Arrays.asList(
        "UP", "DOWN", "LEFT", "RIGHT",
        "BKSP", "DEL",
        "PGUP", "PGDN");

    /** Defines the {@link KeyEvent} for common keys. */
    public static final Map<String, Integer> PRIMARY_KEY_CODES_FOR_STRINGS = createPrimaryKeyCodes();

    private static Map<String, Integer> createPrimaryKeyCodes() {
        Map<String, Integer> map = new HashMap<>();
        map.put("SPACE", KeyEvent.KEYCODE_SPACE);
        map.put("ESC", KeyEvent.KEYCODE_ESCAPE);
        map.put("TAB", KeyEvent.KEYCODE_TAB);
        map.put("HOME", KeyEvent.KEYCODE_MOVE_HOME);
        map.put("END", KeyEvent.KEYCODE_MOVE_END);
        map.put("PGUP", KeyEvent.KEYCODE_PAGE_UP);
        map.put("PGDN", KeyEvent.KEYCODE_PAGE_DOWN);
        map.put("INS", KeyEvent.KEYCODE_INSERT);
        map.put("DEL", KeyEvent.KEYCODE_FORWARD_DEL);
        map.put("BKSP", KeyEvent.KEYCODE_DEL);
        map.put("UP", KeyEvent.KEYCODE_DPAD_UP);
        map.put("LEFT", KeyEvent.KEYCODE_DPAD_LEFT);
        map.put("RIGHT", KeyEvent.KEYCODE_DPAD_RIGHT);
        map.put("DOWN", KeyEvent.KEYCODE_DPAD_DOWN);
        map.put("ENTER", KeyEvent.KEYCODE_ENTER);
        map.put("F1", KeyEvent.KEYCODE_F1);
        map.put("F2", KeyEvent.KEYCODE_F2);
        map.put("F3", KeyEvent.KEYCODE_F3);
        map.put("F4", KeyEvent.KEYCODE_F4);
        map.put("F5", KeyEvent.KEYCODE_F5);
        map.put("F6", KeyEvent.KEYCODE_F6);
        map.put("F7", KeyEvent.KEYCODE_F7);
        map.put("F8", KeyEvent.KEYCODE_F8);
        map.put("F9", KeyEvent.KEYCODE_F9);
        map.put("F10", KeyEvent.KEYCODE_F10);
        map.put("F11", KeyEvent.KEYCODE_F11);
        map.put("F12", KeyEvent.KEYCODE_F12);
        return map;
    }

    /**
     * HashMap that implements Python dict.get(key, default) function.
     * Default java.util .get(key) is then the same as .get(key, null);
     */
    static class CleverMap<K,V> extends HashMap<K,V> {
        V get(K key, V defaultValue) {
            if (containsKey(key))
                return get(key);
            else
                return defaultValue;
        }
    }

    public static class ExtraKeyDisplayMap extends CleverMap<String, String> {}

    /*
     * Multiple maps are available to quickly change
     * the style of the keys.
     */

    public static class EXTRA_KEY_DISPLAY_MAPS {
        /**
         * Keys are displayed in a natural looking way, like "→" for "RIGHT"
         */
        public static final ExtraKeyDisplayMap CLASSIC_ARROWS_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap WELL_KNOWN_CHARACTERS_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap LESS_KNOWN_CHARACTERS_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap NOT_KNOWN_ISO_CHARACTERS = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap NICER_LOOKING_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap FULL_ISO_CHAR_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap ARROWS_ONLY_CHAR_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap LOTS_OF_ARROWS_CHAR_DISPLAY = new ExtraKeyDisplayMap();
        public static final ExtraKeyDisplayMap DEFAULT_CHAR_DISPLAY = new ExtraKeyDisplayMap();

        static {
            CLASSIC_ARROWS_DISPLAY.put("LEFT", "←");
            CLASSIC_ARROWS_DISPLAY.put("RIGHT", "→");
            CLASSIC_ARROWS_DISPLAY.put("UP", "↑");
            CLASSIC_ARROWS_DISPLAY.put("DOWN", "↓");

            WELL_KNOWN_CHARACTERS_DISPLAY.put("ENTER", "↲");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("TAB", "↹");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("BKSP", "⌫");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("DEL", "⌦");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("DRAWER", "☰");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("KEYBOARD", "⌨");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("PASTE", "⎘");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("SCROLL", "⇳");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("PREFERENCES", "⚙");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("EXIT", "╳");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("MOUSE_HELPER", "\uD83D\uDDB1");
            WELL_KNOWN_CHARACTERS_DISPLAY.put("STYLUS_HELPER", "\uD83D\uDD8C");

            LESS_KNOWN_CHARACTERS_DISPLAY.put("HOME", "⇱");
            LESS_KNOWN_CHARACTERS_DISPLAY.put("END", "⇲");
            LESS_KNOWN_CHARACTERS_DISPLAY.put("PGUP", "⇑");
            LESS_KNOWN_CHARACTERS_DISPLAY.put("PGDN", "⇓");

            NOT_KNOWN_ISO_CHARACTERS.put("CTRL", "⎈");
            NOT_KNOWN_ISO_CHARACTERS.put("ALT", "⎇");
            NOT_KNOWN_ISO_CHARACTERS.put("ESC", "⎋");

            NICER_LOOKING_DISPLAY.put("-", "―");

            FULL_ISO_CHAR_DISPLAY.putAll(CLASSIC_ARROWS_DISPLAY);
            FULL_ISO_CHAR_DISPLAY.putAll(WELL_KNOWN_CHARACTERS_DISPLAY);
            FULL_ISO_CHAR_DISPLAY.putAll(LESS_KNOWN_CHARACTERS_DISPLAY);
            FULL_ISO_CHAR_DISPLAY.putAll(NICER_LOOKING_DISPLAY);
            FULL_ISO_CHAR_DISPLAY.putAll(NOT_KNOWN_ISO_CHARACTERS);

            ARROWS_ONLY_CHAR_DISPLAY.putAll(CLASSIC_ARROWS_DISPLAY);
            ARROWS_ONLY_CHAR_DISPLAY.putAll(NICER_LOOKING_DISPLAY);

            LOTS_OF_ARROWS_CHAR_DISPLAY.putAll(CLASSIC_ARROWS_DISPLAY);
            LOTS_OF_ARROWS_CHAR_DISPLAY.putAll(WELL_KNOWN_CHARACTERS_DISPLAY);
            LOTS_OF_ARROWS_CHAR_DISPLAY.putAll(LESS_KNOWN_CHARACTERS_DISPLAY);
            LOTS_OF_ARROWS_CHAR_DISPLAY.putAll(NICER_LOOKING_DISPLAY);

            DEFAULT_CHAR_DISPLAY.putAll(CLASSIC_ARROWS_DISPLAY);
            DEFAULT_CHAR_DISPLAY.putAll(WELL_KNOWN_CHARACTERS_DISPLAY);
            DEFAULT_CHAR_DISPLAY.putAll(NICER_LOOKING_DISPLAY);
        }
    }

    /**
     * Aliases for the keys
     */
    public static final ExtraKeyDisplayMap CONTROL_CHARS_ALIASES = createControlCharsAliases();

    private static ExtraKeyDisplayMap createControlCharsAliases() {
        ExtraKeyDisplayMap map = new ExtraKeyDisplayMap();
        map.put("ESCAPE", "ESC");
        map.put("CONTROL", "CTRL");
        map.put("SHFT", "SHIFT");
        map.put("RETURN", "ENTER");
        map.put("FUNCTION", "FN");
        map.put("LT", "LEFT");
        map.put("RT", "RIGHT");
        map.put("DN", "DOWN");
        map.put("PAGEUP", "PGUP");
        map.put("PAGE_UP", "PGUP");
        map.put("PAGE UP", "PGUP");
        map.put("PAGE-UP", "PGUP");
        map.put("PAGEDOWN", "PGDN");
        map.put("PAGE_DOWN", "PGDN");
        map.put("PAGE-DOWN", "PGDN");
        map.put("DELETE", "DEL");
        map.put("BACKSPACE", "BKSP");
        map.put("BACKSLASH", "\\");
        map.put("QUOTE", "\"");
        map.put("APOSTROPHE", "'");
        return map;
    }
}
