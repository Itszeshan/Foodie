package com.project.zeshan.foodie.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.project.zeshan.foodie.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "EatitDB.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts()
    {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);

        Cursor c = qb.query(database, sqlSelect, null, null, null , null, null);

        final List<Order> result = new ArrayList<>();

        if(c.moveToFirst())
        {
            do{
                String pId = c.getString(c.getColumnIndex("ProductId"));
                String pName = c.getString(c.getColumnIndex("ProductName"));
                String qty = c.getString(c.getColumnIndex("Quantity"));
                String price = c.getString(c.getColumnIndex("Price"));
                String discount = c.getString(c.getColumnIndex("Discount"));

                result.add(new Order(pId,pName,qty,price,discount));
            }while (c.moveToNext());
        }

        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount) VALUES (%s,'%s',%s,%s,%s)",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount());

        db.execSQL(query);

    }

    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);
    }

    public void addToFav(String foodId)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES ('%s');", foodId);
        db.execSQL(query);
    }

    public void removeToFav(String foodId)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId = '%s';", foodId);
        db.execSQL(query);
    }

    public boolean isFav(String foodId)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId = '%s';", foodId);
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        else
        {
            cursor.close();
            return true;
        }

    }

    public int getCartCount() {
        int count = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext()){
            count = cursor.getInt(0);
        }

        return count;
    }

}

