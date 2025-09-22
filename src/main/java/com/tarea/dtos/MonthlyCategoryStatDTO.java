package com.tarea.dtos;

public class MonthlyCategoryStatDTO {
    private int year;
    private int month;                  
    private String category;

    private int totalCompletions;       
    private int uniqueDays;             
    private double avgPerActiveDay;     

    private int totalDurationMinutes;   
    private double activeDayRatio;      

     
    private int weeksActive;            
    private double avgPerActiveWeek;    

    public MonthlyCategoryStatDTO() {}

    public MonthlyCategoryStatDTO(int year, int month, String category,
                                  int totalCompletions, int uniqueDays,
                                  double avgPerActiveDay, int totalDurationMinutes,
                                  double activeDayRatio,
                                  int weeksActive, double avgPerActiveWeek) {
        this.year = year;
        this.month = month;
        this.category = category;
        this.totalCompletions = totalCompletions;
        this.uniqueDays = uniqueDays;
        this.avgPerActiveDay = avgPerActiveDay;
        this.totalDurationMinutes = totalDurationMinutes;
        this.activeDayRatio = activeDayRatio;
        this.weeksActive = weeksActive;
        this.avgPerActiveWeek = avgPerActiveWeek;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTotalCompletions() { return totalCompletions; }
    public void setTotalCompletions(int totalCompletions) { this.totalCompletions = totalCompletions; }

    public int getUniqueDays() { return uniqueDays; }
    public void setUniqueDays(int uniqueDays) { this.uniqueDays = uniqueDays; }

    public double getAvgPerActiveDay() { return avgPerActiveDay; }
    public void setAvgPerActiveDay(double avgPerActiveDay) { this.avgPerActiveDay = avgPerActiveDay; }

    public int getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(int totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public double getActiveDayRatio() { return activeDayRatio; }
    public void setActiveDayRatio(double activeDayRatio) { this.activeDayRatio = activeDayRatio; }

    public int getWeeksActive() { return weeksActive; }
    public void setWeeksActive(int weeksActive) { this.weeksActive = weeksActive; }

    public double getAvgPerActiveWeek() { return avgPerActiveWeek; }
    public void setAvgPerActiveWeek(double avgPerActiveWeek) { this.avgPerActiveWeek = avgPerActiveWeek; }
}
