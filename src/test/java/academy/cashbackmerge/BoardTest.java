package academy.cashbackmerge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.cashbackmerge.model.Board;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {
    @Test
    @DisplayName("Метод set отклоняет отрицательное значение кэшбэка")
    void setNegativeCashback() {
        Board board = new Board();

        assertThatThrownBy(() -> board.set(0, 0, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Board    |  Cashback не может быть отрицательный");

        assertThat(board.get(0, 0)).isZero();
    }
}
