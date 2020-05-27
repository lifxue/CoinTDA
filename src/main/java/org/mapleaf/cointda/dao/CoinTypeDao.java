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
package org.mapleaf.cointda.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapleaf.cointda.bean.CoinMarketCapIdBean;
import org.mapleaf.cointda.bean.property.CoinTypeFXC;
import org.mapleaf.cointda.pool.DBHelper;

/**
 *
 * @author xuelf
 */
public class CoinTypeDao {

    private static final Logger logger = LogManager.getLogger(
            CoinTypeDao.class.getName());

    public CoinTypeDao() {

    }

    /**
     * 批量修改可用参数is_active
     *
     * @param map
     * @return
     */
    public static int batchUpdate(Map<Integer, Integer> map) {
        String sql = "update TAB_CoinMarketCap_id_map set "
                + "is_active=?"
                + " where id=?";
        Object[][] params = new Object[map.size()][];
        int i = 0;
        for (Integer id : map.keySet()) {
            Object[] param = new Object[2];
            param[0] = map.get(id);
            param[1] = id;
            params[i] = param;
            i++;
        }
        return DBHelper.batch(sql, params).length;
    }
    
    /**
     * 
     * @param list
     * @return 
     */
    public static int batchUpdate(List<Integer> list) {
        String sql = "update TAB_CoinMarketCap_id_map set "
                + "is_active=1"
                + " where id=?";
        Object[][] params = new Object[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            Object[] param = new Object[1];
            param[0] = list.get(i);
            params[i] = param;
        }
        return DBHelper.batch(sql, params).length;
    }
    

    /**
     * 查询数据
     *
     * @param symbol
     * @return 返回 list
     */
    public static List<CoinTypeFXC> queryBySymbol(String symbol) {
        String sql = "select * from TAB_CoinMarketCap_id_map "
                + "where symbol like '%" + symbol + "%' "
                + "order by rank";
        List<CoinMarketCapIdBean> list = DBHelper.queryList(
                CoinMarketCapIdBean.class, sql);
        List<CoinTypeFXC> ctList = new ArrayList<>();
        list.stream().map((bean) -> {
            CoinTypeFXC coin = new CoinTypeFXC();
            coin.setId(bean.getId().toString());
            coin.setSelect(bean.getIs_active().equals(1));
            coin.setName(bean.getName());
            coin.setSymbol(bean.getSymbol());
            coin.setRank(bean.getRank().toString());
            coin.setDate(bean.getLast_historical_data());
            return coin;
        }).forEachOrdered((coin) -> {
            ctList.add(coin);
        });
        return ctList;
    }

    /**
     * 查询全部数据
     *
     * @return 返回 list
     */
    public static List<CoinTypeFXC> queryAll() {
        String sql = "select * from TAB_CoinMarketCap_id_map order by rank";
        List<CoinMarketCapIdBean> list = DBHelper.queryList(
                CoinMarketCapIdBean.class, sql);
        List<CoinTypeFXC> ctList = new ArrayList<>();
        list.stream().map((bean) -> {
            CoinTypeFXC coin = new CoinTypeFXC();
            coin.setId(bean.getId().toString());
            coin.setSelect(bean.getIs_active().equals(1));
            coin.setName(bean.getName());
            coin.setSymbol(bean.getSymbol());
            coin.setRank(bean.getRank().toString());
            coin.setDate(bean.getLast_historical_data());
            return coin;
        }).forEachOrdered((coin) -> {
            ctList.add(coin);
        });
        return ctList;
    }

     /**
     * 查询全部可用Coin的简称
     *
     * @return 返回 简称list
     */
    public static List<String> queryCurSymbol() {
        String sql = "select symbol from TAB_CoinMarketCap_id_map "
                + "where is_active=1 order by rank";
        return DBHelper.queryColumn(sql);
    }
    
    public static List<Integer> queryCurID() {
//        List<CoinMarketCapIdBean> list = queryCur();
//        List<Integer> idList = new ArrayList<>();
//        if (list != null) {
//            list.forEach(action -> {
//                idList.add(action.getId());
//            });
//        }
//        return idList;
        String sql = "select id from TAB_CoinMarketCap_id_map "
                + "where is_active=1 order by rank";
        return DBHelper.queryColumn(sql);
    }

    /**
     * 查询当前被选中可使用的coin
     *
     * @return
     */
    public static List<CoinMarketCapIdBean> queryCur() {
        String sql = "select * from TAB_CoinMarketCap_id_map "
                + "where is_active=1 order by rank";
        return DBHelper.queryList(CoinMarketCapIdBean.class, sql);
    }

    /**
     * 查询当前被选中可使用的coin
     *
     * @return
     */
    public static List<CoinTypeFXC> queryCurFXC() {
        List<CoinMarketCapIdBean> list = queryCur();
        List<CoinTypeFXC> ctList = new ArrayList<>();
        list.stream().map((bean) -> {
            CoinTypeFXC coin = new CoinTypeFXC();
            coin.setId(bean.getId().toString());
            coin.setSelect(bean.getIs_active().equals(1));
            coin.setName(bean.getName());
            coin.setSymbol(bean.getSymbol());
            coin.setRank(bean.getRank().toString());
            coin.setDate(bean.getLast_historical_data());
            return coin;
        }).forEachOrdered((coin) -> {
            ctList.add(coin);
        });
        return ctList;
    }

    public static void main(String[] args) {
        CoinTypeDao.queryAll().forEach(action -> logger.info("CoinType::" + action));
    }
}
