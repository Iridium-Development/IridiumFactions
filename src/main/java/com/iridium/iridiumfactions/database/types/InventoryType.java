package com.iridium.iridiumfactions.database.types;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class InventoryType extends StringType {

    private static final InventoryType instance = new InventoryType();

    public static InventoryType getSingleton() {
        return instance;
    }

    protected InventoryType() {
        super(SqlType.STRING, new Class<?>[]{Inventory.class});
    }

    //TODO: Implement this
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return Bukkit.getServer().createInventory(null, 9);
    }

    @Override
    public Object javaToSqlArg(FieldType fieldType, Object object) {
        return "";
    }

}
