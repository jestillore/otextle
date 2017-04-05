package xyz.ejillberth.otextle.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "connected")
public class Connected extends Model {

    @Column(name = "tom")
    public String tom;

    @Column(name = "jerry")
    public String jerry;

}
