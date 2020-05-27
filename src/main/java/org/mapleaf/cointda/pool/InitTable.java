/*
 * Copyright 2019 xuelf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mapleaf.cointda.pool;

/**
 *
 * @author xuelf
 */
public class InitTable {

    /**
     * Creates a new instance of InitTable
     */
    public InitTable() {

    }

    /**
     * 删除数据库表
     */
    public static void dropTable() {
        String sql;
        sql = "drop table tab_tradeinfo";
        DBHelper.dropTable(sql);
        sql = "drop table TAB_CoinMarketCap_id_map";
        DBHelper.dropTable(sql);
        sql = "drop table TAB_CoinMarketCap_listings";
        DBHelper.dropTable(sql);
        sql = "drop table tab_quotesLatest";
        DBHelper.dropTable(sql);
    }

    /**
     * 创建数据库
     *
     */
    public static void createTable() {
        String sql;

        //交易信息表
        if (!DBHelper.doesTableExist("tab_tradeinfo")) {
            sql = "CREATE TABLE tab_tradeinfo ("
                    + "id integer generated by default as identity,"
                    + "base_id integer NOT NULL,"
                    + "base_symbol VARCHAR(20) NOT NULL,"
                    + "quote_id integer NOT NULL,"
                    + "quote_symbol VARCHAR(20) NOT NULL,"
                    + "sale_or_buy VARCHAR(10) NOT NULL,"
                    + "price  VARCHAR(100) NOT NULL,"
                    + "base_num  VARCHAR(100) NOT NULL,"
                    + "quote_num  VARCHAR(100) NOT NULL,"
                    + "trade_date VARCHAR(100) NOT NULL,"
                    + "primary key (id))";
            DBHelper.createTable(sql);
        }

        //品种信息
        if (!DBHelper.doesTableExist("TAB_CoinMarketCap_id_map")) {
            sql = "CREATE TABLE TAB_CoinMarketCap_id_map  ("
                    + "id integer NOT NULL,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "symbol VARCHAR(20) NOT NULL,"
                    + "slug VARCHAR(100) NOT NULL,"
                    + "is_active integer NOT NULL,"
                    + "rank integer NOT NULL,"
                    + "first_historical_data VARCHAR(100),"
                    + "last_historical_data VARCHAR(100),"
                    + "platform_id integer,"
                    + "token_address VARCHAR(100),"
                    + "primary key (id))";
            DBHelper.createTable(sql);
        }
        //最新价格全表
        if (!DBHelper.doesTableExist("tab_CoinMarketCap_listings")) {
            sql = "CREATE TABLE tab_CoinMarketCap_listings  ("
                    + "id integer NOT NULL,"
                    + "name VARCHAR(100),"
                    + "symbol VARCHAR(20),"
                    + "slug VARCHAR(100),"
                    + "cmc_rank integer,"
                    + "date_added VARCHAR(100),"
                    + "platform_id integer,"
                    + "token_address VARCHAR(100),"
                    + "numMarketPairs integer,"
                    + "maxSupply VARCHAR(100),"
                    + "circulatingSupply VARCHAR(100),"
                    + "totalSupply VARCHAR(100),"
                    + "price VARCHAR(100),"
                    + "volume_24h VARCHAR(100),"
                    + "percent_change_1h VARCHAR(100),"
                    + "percent_change_24h VARCHAR(100),"
                    + "percent_change_7d VARCHAR(100),"
                    + "marketCap VARCHAR(100),"
                    + "lastUpdated VARCHAR(100),"
                    + "primary key (id))";
            DBHelper.createTable(sql);
        }
        //最新价格表  
        if (!DBHelper.doesTableExist("tab_quotesLatest")) {
            sql = "CREATE TABLE tab_quotesLatest  ("
                    + "id integer NOT NULL,"
                    + "name VARCHAR(100),"
                    + "symbol VARCHAR(20),"
                    + "slug VARCHAR(100),"
                    + "num_market_pairs integer,"
                    + "date_added VARCHAR(100),"
                    + "max_supply VARCHAR(100),"
                    + "circulating_supply VARCHAR(100),"
                    + "total_supply VARCHAR(100),"
                    + "is_active integer,"
                    + "platform_id integer,"
                    + "token_address VARCHAR(100),"
                    + "cmc_rank integer,"
                    + "is_fiat integer,"
                    + "lastUpdated VARCHAR(100),"
                    + "price VARCHAR(100),"
                    + "volume_24h VARCHAR(100),"
                    + "percent_change_1h VARCHAR(100),"
                    + "percent_change_24h VARCHAR(100),"
                    + "percent_change_7d VARCHAR(100),"
                    + "market_cap VARCHAR(100),"
                    + "primary key (id))";
            DBHelper.createTable(sql);
        }

    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        InitTable.createTable();
    }
}
