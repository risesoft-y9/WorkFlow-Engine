package net.risesoft.util;

public class TaxCalculator {

    private static final double TAX_FREE_THRESHOLD = 800.0; // 免税阈值
    private static final double LOWER_THRESHOLD = 4000.0; // 低档税率阈值
    private static final double MIDDLE_THRESHOLD = 20000.0; // 中档税率阈值
    private static final double DEDUCTION_AMOUNT = 800.0; // 固定扣除额
    private static final double DEDUCTION_RATE = 0.2; // 费用扣除率
    private static final double LOW_TAX_RATE = 0.2; // 低税率
    private static final double HIGH_TAX_RATE = 0.3; // 高税率

    /**
     * 根据收入金额计算应缴税额
     *
     * @param income 收入金额
     * @return 应缴税额
     */
    public static double calculateTax(double income) {
        if (income <= TAX_FREE_THRESHOLD) {
            // 800元以下不计税
            return 0.0;
        } else if (income <= LOWER_THRESHOLD) {
            // 800-4000元: 减除800元后按20%税率
            return (income - DEDUCTION_AMOUNT) * LOW_TAX_RATE;
        } else if (income <= MIDDLE_THRESHOLD) {
            // 4000-20000元: 减除20%费用后按20%税率
            return income * (1 - DEDUCTION_RATE) * LOW_TAX_RATE;
        } else {
            // 20000元以上: 减除20%费用后按30%税率
            return income * (1 - DEDUCTION_RATE) * HIGH_TAX_RATE;
        }
    }

    /**
     * 计算税后收入(实发金额)
     *
     * @param income 应发金额
     * @return 实发金额
     */
    public static double calculateAfterTaxIncome(double income) {
        return income - calculateTax(income);
    }

    /**
     * 更精确的反向计算方法（解析法）
     *
     * @param afterTaxIncome 实发金额
     * @return 应发金额
     */
    public static double calculatePreTaxIncomeExact(double afterTaxIncome) {
        if (afterTaxIncome <= TAX_FREE_THRESHOLD) {
            return afterTaxIncome;
        }
        // 尝试在不同税率区间求解
        // 区间1: 800-4000 (税后范围: 800-3360)
        if (afterTaxIncome <= 3360) {
            // 实发 = 应发 - (应发 - 800) * 0.2 = 0.8 * 应发 + 160
            // 应发 = (实发 - 160) / 0.8
            return (afterTaxIncome - 160) / 0.8;
        }
        // 区间2: 4000-20000 (税后范围: 3360-16800)
        if (afterTaxIncome <= 16800) {
            // 实发 = 应发 - 应发 * 0.8 * 0.2 = 0.84 * 应发
            // 应发 = 实发 / 0.84
            return afterTaxIncome / 0.84;
        }
        // 区间3: 20000以上 (税后范围: 16800以上)
        // 实发 = 应发 - 应发 * 0.8 * 0.3 = 0.76 * 应发
        // 应发 = 实发 / 0.76
        return afterTaxIncome / 0.76;
    }

    public static void main(String[] args) {
        double[] testIncomes = {500, 800, 1000, 4000, 5000, 20000, 25000};
        System.out.println("收入计税测试:");
        System.out.println("=====================================");
        for (double income : testIncomes) {
            double tax = calculateTax(income);
            double afterTax = calculateAfterTaxIncome(income);
            double preTax = calculatePreTaxIncomeExact(afterTax);
            System.out.printf("应发: %.2f元 -> 税额: %.2f元 -> 实发: %.2f元 -> 反推应发: %.2f元%n", income, tax, afterTax, preTax);
        }
        System.out.println("\n通过实发金额反推应发金额测试:");
        System.out.println("=====================================");
        double[] testAfterTaxIncomes = {700, 800, 900, 3000, 4000, 15000, 20000};
        for (double afterTax : testAfterTaxIncomes) {
            double preTax = calculatePreTaxIncomeExact(afterTax);
            double tax = calculateTax(preTax);
            System.out.printf("应发: %.2f元  -> 税额: %.2f元-> 实发: %.2f元%n", preTax, tax, afterTax);
        }
    }
}