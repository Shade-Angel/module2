package academy.cashbackmerge.mandatory.mr2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.cashbackmerge.model.Board;
import academy.cashbackmerge.model.Move;
import academy.cashbackmerge.model.MoveResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Обязательные тесты: правила слияния плиток. */
@DisplayName("MR2. Слияние плиток")
class MergeRulesTest {

    private Board parseBoard(String rowStr) {
        Board b = new Board();
        String[] parts = rowStr.split(",");
        for (int i = 0; i < 4; i++) {
            String p = parts[i].trim();
            if (!".".equals(p)) {
                b.set(0, i, Integer.parseInt(p));
            }
        }
        return b;
    }

    @ParameterizedTest
    @CsvSource({
        "'1,1,.,.', '2,.,.,.'",
        "'1,1,1,.', '2,1,.,.'",
        "'1,1,1,1', '2,2,.,.'",
        "'2,2,4,4', '4,8,.,.'",
    })
    @DisplayName("Сдвиг влево: {0} -> {1}")
    void rowShiftsLeftAndMerges(String before, String expected) {
        Board b = parseBoard(before);
        MoveResult res = b.applyMove(Move.LEFT);
        Board expect = parseBoard(expected);
        assertEquals(expect, res.newBoard());
    }

    @Test
    @DisplayName("Одна плитка не сливается дважды за ход: [1,1,2,.] -> [2,2,.,.]")
    void tileDoesNotMergeTwiceInOneMove() {
        Board b = parseBoard("1,1,2,.");
        MoveResult res = b.applyMove(Move.LEFT);
        Board expected = parseBoard("2,2,.,.");
        assertEquals(expected, res.newBoard());
        assertTrue(res.merge());
    }

    @Test
    @DisplayName("Слияние работает во всех четырёх направлениях")
    void mergeWorksInAllDirections() {
        Board bLeft = parseBoard("1,1,.,.");
        assertEquals(parseBoard("2,.,.,."), bLeft.applyMove(Move.LEFT).newBoard());

        Board bRight = parseBoard(".,.,1,1");
        assertEquals(parseBoard(".,.,.,2"), bRight.applyMove(Move.RIGHT).newBoard());

        Board bUp = new Board();
        bUp.set(0, 0, 1);
        bUp.set(1, 0, 1);
        Board expectedUp = new Board();
        expectedUp.set(0, 0, 2);
        assertEquals(expectedUp, bUp.applyMove(Move.UP).newBoard());

        Board bDown = new Board();
        bDown.set(2, 0, 1);
        bDown.set(3, 0, 1);
        Board expectedDown = new Board();
        expectedDown.set(3, 0, 2);
        assertEquals(expectedDown, bDown.applyMove(Move.DOWN).newBoard());
    }
}
