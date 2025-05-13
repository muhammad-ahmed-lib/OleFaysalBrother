package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetProfit {
    @SerializedName("total_income_share")
    @Expose
    private Integer totalIncomeShare;
    @SerializedName("total_deposits")
    @Expose
    private Integer totalDeposits;
    @SerializedName("total_withdrawls")
    @Expose
    private Integer totalWithdrawls;
    @SerializedName("net_profit")
    @Expose
    private Integer netProfit;

    public Integer getTotalIncomeShare() {
        return totalIncomeShare;
    }

    public void setTotalIncomeShare(Integer totalIncomeShare) {
        this.totalIncomeShare = totalIncomeShare;
    }

    public Integer getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(Integer totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public Integer getTotalWithdrawls() {
        return totalWithdrawls;
    }

    public void setTotalWithdrawls(Integer totalWithdrawls) {
        this.totalWithdrawls = totalWithdrawls;
    }

    public Integer getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(Integer netProfit) {
        this.netProfit = netProfit;
    }
}
