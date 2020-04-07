/*
 * Copyright 2020 lif.
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
package org.mapleaf.cointda.modules.export;

import com.dlsc.workbenchfx.Workbench;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapleaf.cointda.bean.TradeDataBean;
import org.mapleaf.cointda.dao.TradeDataDao;
import org.mapleaf.cointda.util.CSVHelper;

/**
 *
 * @author lif
 */
public class ExportTradeData {

    private static final Logger logger = LogManager.getLogger(ExportTradeData.class.getName());
    private final Workbench workbench;

    public ExportTradeData(Workbench workbench) {
        this.workbench = workbench;
    }

    public void handleExportData() {
        List<TradeDataBean> list = TradeDataDao.queryAll();
        List<String[]> data = new ArrayList<>();
        String[] headers = {"id", "coid_id", "简称", "买卖", "单价", "数量", "总价", "交易时间"};
        if (list == null) {
            workbench.showErrorDialog(
                    "错误",
                    "导出数据失败！",
                    "没有交易数据！",
                    buttonType -> {
                    }
            );
            return;
        }
        list.stream().map((bean) -> {
            String[] str = new String[8];
            str[0] = bean.getId().toString();
            str[1] = bean.getCoin_id().toString();
            str[2] = bean.getCoin_symbol();
            str[3] = bean.getSale_or_buy();
            str[4] = bean.getPrice();
            str[5] = bean.getNum();
            str[6] = bean.getTotal_price();
            str[7] = bean.getTrade_date();
            return str;
        }).forEachOrdered((str) -> {
            data.add(str);
        });
        FileChooser fileChooser = new FileChooser();
        //文档类型过滤器
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("txt files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(workbench.getScene().getWindow());
        if (file != null) {
            CSVHelper.writeCsv(headers, data, file);
        }
    }
}