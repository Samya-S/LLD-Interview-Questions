package domain;

public class SearchFilter {
    private String city;
    private String country;
    private Long checkInDateUtc;
    private Long checkOutDateUtc;
    private String bedType;
    private Long minPrice;
    private Long maxPrice;

    public SearchFilter() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getCheckInDateUtc() {
        return checkInDateUtc;
    }

    public void setCheckInDateUtc(Long checkInDateUtc) {
        this.checkInDateUtc = checkInDateUtc;
    }

    public Long getCheckOutDateUtc() {
        return checkOutDateUtc;
    }

    public void setCheckOutDateUtc(Long checkOutDateUtc) {
        this.checkOutDateUtc = checkOutDateUtc;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public Long getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Long minPrice) {
        this.minPrice = minPrice;
    }

    public Long getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Long maxPrice) {
        this.maxPrice = maxPrice;
    }
}
