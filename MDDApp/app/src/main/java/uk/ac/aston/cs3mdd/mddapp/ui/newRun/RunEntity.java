package uk.ac.aston.cs3mdd.mddapp.ui.newRun;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "run_table")
public class RunEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "formatted_date")
    public String formattedDate;

    @ColumnInfo(name = "start_time")
    public long startTime;

    @ColumnInfo(name = "end_time")
    public long endTime;

    @ColumnInfo(name = "distance")
    public float distance;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "pace")
    public float pace;

    @ColumnInfo(name = "start_latitude")
    public double startLatitude;

    @ColumnInfo(name = "start_longitude")
    public double startLongitude;

    @ColumnInfo(name = "end_latitude")
    public double endLatitude;

    @ColumnInfo(name = "end_longitude")
    public double endLongitude;

    // Store the polyline points as a JSON string
    @ColumnInfo(name = "polyline_points")
    public String polylinePoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return formattedDate;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getPace() {
        return pace;
    }

    public void setPace(float pace) {
        this.pace = pace;
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public double getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(double endLatitude) {
        this.endLatitude = endLatitude;
    }

    public double getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(double endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getPolylinePoints() {
        return polylinePoints;
    }

    public void setPolylinePoints(String polylinePoints) {
        this.polylinePoints = polylinePoints;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
