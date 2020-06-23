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
package org.mapleaf.cointda.modules.patable;

import com.dlsc.workbenchfx.Workbench;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapleaf.cointda.bean.property.TradeDataFXC;
import org.mapleaf.cointda.dao.CoinTypeDao;
import org.mapleaf.cointda.dao.PATableDao;
import org.mapleaf.cointda.util.DateHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author xuelf
 */
public class PATableViewController implements Initializable {

  private static final Logger logger = LogManager.getLogger(PATableViewController.class.getName());
  /** The data as an observable list of TradeData. */
  private final ObservableList<TradeDataFXC> tradeDataList;
  private final List<String> coinSymbolList;
  @FXML private DatePicker startDatePicker;
  @FXML private DatePicker endDatePicker;
  @FXML private ChoiceBox<String> typeChoiceBox;
  @FXML private Label coinTypeLabel;
  @FXML private Label paLabel;
  @FXML private Label numTotalLabel;
  @FXML private Label nowPriceTotalLabel;
  @FXML private Label PriceTotalLabel;
  @FXML private Label nowpaLabel;
  @FXML private TableView<TradeDataFXC> tradeDataTable;
  @FXML private TableColumn<TradeDataFXC, Integer> idCol;
  @FXML private TableColumn<TradeDataFXC, Integer> coinIdCol;
  @FXML private TableColumn<TradeDataFXC, String> symbolPairsCol;
  @FXML private TableColumn<TradeDataFXC, String> buyOrSaleCol;
  @FXML private TableColumn<TradeDataFXC, String> priceCol;
  @FXML private TableColumn<TradeDataFXC, String> baseNumCol;
  @FXML private TableColumn<TradeDataFXC, String> quoteNumCol;
  @FXML private TableColumn<TradeDataFXC, String> dateCol;
  private Workbench workbench;

  public PATableViewController() {
    tradeDataList = FXCollections.observableArrayList();
    List<TradeDataFXC> list = PATableDao.queryAllFXC();
    tradeDataList.addAll(list);

    coinSymbolList = CoinTypeDao.queryCurSymbol();
  }

  /**
   * @Description: Initializes the controller class.
   *
   * @param url 1
   * @param rb 2
   * @return: void
   * @author: mapleaf
   * @date: 2020/6/23 18:56
   */
  @Override
  public void initialize(URL url, ResourceBundle rb) {
    tradeDataTable.setItems(tradeDataList);

    idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    coinIdCol.setCellValueFactory(new PropertyValueFactory<>("coinId"));
    symbolPairsCol.setCellValueFactory(cellData -> cellData.getValue().symbolPairsProperty());
    buyOrSaleCol.setCellValueFactory(cellData -> cellData.getValue().saleOrBuyProperty());
    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    baseNumCol.setCellValueFactory(new PropertyValueFactory<>("baseNum"));
    quoteNumCol.setCellValueFactory(new PropertyValueFactory<>("quoteNum"));
    dateCol.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

    typeChoiceBox.setItems(FXCollections.observableArrayList(coinSymbolList));
    typeChoiceBox.setTooltip(new Tooltip("选择交易品种"));

    startDatePicker.setConverter(DateHelper.CONVERTER);
    startDatePicker.setTooltip(new Tooltip("选择初始时间"));
    startDatePicker.setEditable(false);
    // startDatePicker.setValue(LocalDate.now());
    startDatePicker.setValue(LocalDate.of(2009, 1, 3));
    endDatePicker.setConverter(DateHelper.CONVERTER);
    endDatePicker.setTooltip(new Tooltip("选择结束时间"));
    endDatePicker.setEditable(false);
    endDatePicker.setValue(LocalDate.now());
  }

  @FXML
  private void handleSearchOnAction(ActionEvent event) {
    if (isInputValid()) {
      String coinSymbol = this.typeChoiceBox.getValue();
      String startDate = DateHelper.toString(this.startDatePicker.getValue());
      String endDate = DateHelper.toString(this.endDatePicker.getValue());

      List<TradeDataFXC> list = PATableDao.queryBy(coinSymbol, startDate, endDate);
      tradeDataList.clear();
      tradeDataList.addAll(list);

      Map<String, String> mapTotal = getPAData(coinSymbol, list);
      this.coinTypeLabel.setText(coinSymbol);
      this.nowPriceTotalLabel.setText(mapTotal.get("nowPriceTotal"));
      nowPriceTotalLabel.setTooltip(new Tooltip(mapTotal.get("nowPriceTotal")));
      this.paLabel.setText(mapTotal.get("paPrice"));
      paLabel.setTooltip(new Tooltip(mapTotal.get("paPrice")));
      this.numTotalLabel.setText(mapTotal.get("numTotal"));
      numTotalLabel.setTooltip(new Tooltip(mapTotal.get("numTotal")));
      nowpaLabel.setText(mapTotal.get("nowPrice"));
      nowpaLabel.setTooltip(new Tooltip(mapTotal.get("nowPrice")));
      PriceTotalLabel.setText(mapTotal.get("paPriceTotal"));
      PriceTotalLabel.setTooltip(new Tooltip(mapTotal.get("paPriceTotal")));
    }
  }

  private Map<String, String> getPAData(String strCoinSymbol, List<TradeDataFXC> tradeDataList) {
    Map<String, String> map = new HashMap<>();
    BigDecimal sale = new BigDecimal("0");
    BigDecimal numTotal = new BigDecimal("0");
    BigDecimal buy = new BigDecimal("0");
    if (tradeDataList != null) {
      for (TradeDataFXC td : tradeDataList) {
        if (td.getSaleOrBuy().equals("买")) {
          numTotal = numTotal.add(new BigDecimal(td.getBaseNum()));
          buy = buy.add(new BigDecimal(td.getQuoteNum()));
        } else if (td.getSaleOrBuy().equals("卖")) {
          numTotal = numTotal.subtract(new BigDecimal(td.getBaseNum()));
          sale = sale.add(new BigDecimal(td.getQuoteNum()));
        }
      }
    }
    BigDecimal curPrice = new BigDecimal(PATableDao.queryBySymbol(strCoinSymbol).getPrice());
    BigDecimal paPrice = new BigDecimal("0");
    BigDecimal paPriceTotal = buy.subtract(sale);
    if (numTotal.compareTo(new BigDecimal("0")) > 0) {
      paPrice = paPriceTotal.divide(numTotal, 12, RoundingMode.HALF_UP);
    }
    map.put("numTotal", numTotal.setScale(12, RoundingMode.HALF_UP).toString());
    map.put(
        "nowPriceTotal", numTotal.multiply(curPrice).setScale(12, RoundingMode.HALF_UP).toString());
    map.put("nowPrice", curPrice.setScale(12, RoundingMode.HALF_UP).toString());
    map.put("paPriceTotal", paPriceTotal.setScale(12, RoundingMode.HALF_UP).toString());
    map.put("paPrice", paPrice.toString());
    return map;
  }

  /**
  * @Description: Validates the user inpu.
  * @return: boolean
  * @author: mapleaf
  * @date: 2020/6/23 18:58
  */
  private boolean isInputValid() {
    String errorMessage = "";

    if (typeChoiceBox.getValue() == null || typeChoiceBox.getValue().length() == 0) {
      errorMessage += "无效的类别!\n";
    }
    if (!DateHelper.validDate(DateHelper.toString(startDatePicker.getValue()))
        || startDatePicker.getValue() == null) {
      errorMessage += "无效的时间!\n";
    }
    if (!DateHelper.validDate(DateHelper.toString(endDatePicker.getValue()))
        || endDatePicker.getValue() == null) {
      errorMessage += "无效的时间!\n";
    }

    if (errorMessage.length() == 0) {
      return true;
    } else {
      // Show the error message.
      workbench.showErrorDialog("警告", "无效的字段！", errorMessage, buttonType -> {});
      return false;
    }
  }

  public void setWorkbench(Workbench workbench) {
    this.workbench = workbench;
  }
}
