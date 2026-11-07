package ua.kpi.comsys.collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionHistoryManagerTest {

    private ActionHistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new ActionHistoryManager(3);
    }

    @Test
    void constructor_negativeCapacity_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new ActionHistoryManager(-5));
        assertEquals("maxCapacity must be greater than 0", ex.getMessage());
    }

    @Test
    void constructor_capacityOne_isValid() {
        assertDoesNotThrow(() -> new ActionHistoryManager(1));
    }


    @Test
    void performAction_null_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> manager.performAction(null));
        assertEquals("Action cannot be null or blank", ex.getMessage());
    }

    @Test
    void performAction_empty_throws() {
        assertThrows(IllegalArgumentException.class, () -> manager.performAction(""));
    }

    @Test
    void performAction_blank_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> manager.performAction("   "));
        assertEquals("Action cannot be null or blank", ex.getMessage());
    }

    @Test
    void performAction_addsToTop() {
        manager.performAction("A");
        manager.performAction("B");
        assertEquals("B", manager.peekRecentAction());
        assertEquals(2, manager.getHistorySize());
    }

    @Test
    void performAction_exceedingCapacity_removesOldest() {
        manager.performAction("A");
        manager.performAction("B");
        manager.performAction("C");
        manager.performAction("D");

        assertEquals(3, manager.getHistorySize());
        assertEquals(List.of("D", "C", "B"), manager.getHistory());
    }

    @Test
    void performAction_exactlyAtCapacity_keepsAll() {
        manager.performAction("A");
        manager.performAction("B");
        manager.performAction("C");

        assertEquals(List.of("C", "B", "A"), manager.getHistory());
    }

    @Test
    void performAction_duplicatesAllowed() {
        manager.performAction("A");
        manager.performAction("A");

        assertEquals(List.of("A", "A"), manager.getHistory());
    }


    @Test
    void undo_emptyHistory_returnsNull() {
        assertNull(manager.undo());
    }

    @Test
    void undo_returnsAndRemovesMostRecent() {
        manager.performAction("A");
        manager.performAction("B");

        assertEquals("B", manager.undo());
        assertEquals(1, manager.getHistorySize());
        assertEquals("A", manager.peekRecentAction());
    }

    @Test
    void undo_lifoOrder() {
        manager.performAction("A");
        manager.performAction("B");
        manager.performAction("C");

        assertEquals("C", manager.undo());
        assertEquals("B", manager.undo());
        assertEquals("A", manager.undo());
        assertNull(manager.undo());
    }


    @Test
    void peek_emptyHistory_returnsNull() {
        assertNull(manager.peekRecentAction());
    }

    @Test
    void peek_doesNotRemove() {
        manager.performAction("A");

        assertEquals("A", manager.peekRecentAction());
        assertEquals("A", manager.peekRecentAction());
        assertEquals(1, manager.getHistorySize());
    }


    @Test
    void getHistory_empty_returnsEmptyList() {
        List<String> history = manager.getHistory();
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void getHistory_orderFromNewestToOldest() {
        manager.performAction("A");
        manager.performAction("B");
        manager.performAction("C");

        List<String> history = manager.getHistory();
        assertEquals("C", history.get(0));
        assertEquals("B", history.get(1));
        assertEquals("A", history.get(2));
    }

    @Test
    void getHistory_modifyingReturnedListDoesNotAffectInternalState() {
        manager.performAction("A");
        manager.performAction("B");

        List<String> history = manager.getHistory();
        history.clear();
        history.add("HACK");

        assertEquals(2, manager.getHistorySize());
        assertEquals(List.of("B", "A"), manager.getHistory());
    }


    @Test
    void getHistorySize_initiallyZero() {
        assertEquals(0, manager.getHistorySize());
    }

    @Test
    void clear_removesEverything() {
        manager.performAction("A");
        manager.performAction("B");

        manager.clear();

        assertEquals(0, manager.getHistorySize());
        assertNull(manager.peekRecentAction());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void clear_onEmpty_doesNotThrow() {
        assertDoesNotThrow(() -> manager.clear());
    }

    @Test
    void afterClear_canAddAgain() {
        manager.performAction("A");
        manager.clear();
        manager.performAction("B");

        assertEquals(List.of("B"), manager.getHistory());
    }
}