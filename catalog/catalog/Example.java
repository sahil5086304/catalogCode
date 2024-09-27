import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Example{

    public static void main(String[] args) {
        // Paths to the JSON files for both test cases
        String filePath1 = "testcase1.json"; 
        String filePath2 = "testcase2.json"; 

        try {
            System.out.println("Test Case 1:");
            processTestCase(filePath1);
            System.out.println("\nTest Case 2:");
            processTestCase(filePath2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processTestCase(String filePath) throws Exception {
        // Step 1: Read the JSON file
        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        // Step 2: Decode the Roots
        List<Point> points = decodeRoots(content);

        // Step 3: Calculate the Constant Term c
        double constantTerm = calculateConstantTerm(points);
        System.out.println("Constant Term c: " + constantTerm);
    }

    // Method to decode the roots from the JSON string
    private static List<Point> decodeRoots(String json) {
        List<Point> points = new ArrayList<>();
        
        // Extracting n from the JSON string
        String[] parts = json.split("\"keys\":\\s*\\{")[1].split("\\}")[0].split(",");
        int n = Integer.parseInt(parts[0].split(":")[1].trim());

        for (int i = 1; i <= n; i++) {
            if (json.contains("\"" + i + "\":")) {
                String root = json.split("\"" + i + "\":")[1].split("\\}")[0] + "}";
                int base = Integer.parseInt(root.split("\"base\":")[1].split(",")[0].trim());
                String valueStr = root.split("\"value\":")[1].split("\"")[1];

                long decodedValue = decodeValue(valueStr, base);
                points.add(new Point(i, decodedValue));
            }
        }
        return points;
    }

    // Method to decode the value from a given base to decimal
    private static long decodeValue(String value, int base) {
        long decimalValue = 0;
        int length = value.length();

        for (int i = 0; i < length; i++) {
            char digit = value.charAt(length - 1 - i);
            int digitValue;

            if (Character.isDigit(digit)) {
                digitValue = digit - '0';
            } else {
                digitValue = Character.toUpperCase(digit) - 'A' + 10;
            }

            decimalValue += digitValue * Math.pow(base, i);
        }

        return decimalValue;
    }

    // Method to calculate the constant term c using the matrix method
    private static double calculateConstantTerm(List<Point> points) {
        int n = points.size();
        double[][] matrix = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = (double) (points.get(i).x - points.get(j).x);
                }
            }
            matrix[i][n] = points.get(i).y;
        }

        return gaussianElimination(matrix, n);
    }

    // Gaussian elimination method to solve the linear equations
    private static double gaussianElimination(double[][] matrix, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[i][i])) {
                    double[] temp = matrix[i];
                    matrix[i] = matrix[j];
                    matrix[j] = temp;
                }
            }

            for (int j = i + 1; j < n; j++) {
                double factor = matrix[j][i] / matrix[i][i];
                for (int k = i; k < n + 1; k++) {
                    matrix[j][k] -= factor * matrix[i][k];
                }
            }
        }

        double[] solution = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            solution[i] = matrix[i][n];
            for (int j = i + 1; j < n; j++) {
                solution[i] -= matrix[i][j] * solution[j];
            }
            solution[i] /= matrix[i][i];
        }

        return solution[n - 1];
    }

    // Simple class to hold point data
    static class Point {
        int x;
        long y;

        Point(int x, long y) {
            this.x = x;
            this.y = y;
        }
    }
}
