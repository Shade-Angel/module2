package academy.cashbackmerge.engine;

import java.util.Arrays;

public final class LineProcess {

    private LineProcess() {}

    @SuppressWarnings("ArrayRecordComponent")
    public record Line(int[] values) {
        public Line(int[] values) {
            this.values = Arrays.copyOf(values, values.length);
        }

        public int get(int index) {
            return values[index];
        }

        public int length() {
            return values.length;
        }
    }

    public record LineResult(Line line, boolean moved, boolean merged, long scoreAdded) {}

    public static LineResult process(int[] line) {
        int[] filtered = filterNonZero(line);
        int[] result = new int[line.length];
        int resIdx = 0;
        boolean mergedFlag = false;
        long score = 0;

        for (int i = 0; i < filtered.length; ) {
            if (filtered[i] == 0) {
                i++;
                continue;
            }
            if (i < filtered.length - 1 && filtered[i] == filtered[i + 1]) {
                int mergedVal = filtered[i] * 2;
                result[resIdx++] = mergedVal;
                score += mergedVal;
                mergedFlag = true;
                i += 2;
            } else {
                result[resIdx++] = filtered[i];
                i++;
            }
        }

        boolean moved = hasChanged(line, result);
        return new LineResult(new Line(result), moved, mergedFlag, score);
    }

    private static int[] filterNonZero(int[] line) {
        int[] result = new int[line.length];
        int idx = 0;
        for (int val : line) {
            if (val != 0) {
                result[idx++] = val;
            }
        }
        return result;
    }

    private static boolean hasChanged(int[] original, int[] result) {
        for (int i = 0; i < original.length; i++) {
            if (original[i] != result[i]) {
                return true;
            }
        }
        return false;
    }

    public static int[] reverse(int[] arr) {
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[arr.length - 1 - i];
        }
        return res;
    }
}
