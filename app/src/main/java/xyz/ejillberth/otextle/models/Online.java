package xyz.ejillberth.otextle.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "online")
public class Online extends Model {

    @Column(name = "number")
    public String number;

}
