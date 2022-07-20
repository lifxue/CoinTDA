package org.cointda.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cointda.dto.Platform;
import org.cointda.dto.Status;
import org.cointda.dto.quote.Quote;
import org.cointda.dto.quote.QuotesLatestDto;
import org.cointda.service.IQuotesLatestService;
import org.cointda.service.feignc.IQuotesLatestFeignClient;
import org.cointda.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class QuotesLatestServiceImpl implements IQuotesLatestService {

    @Autowired
    IQuotesLatestFeignClient iQuotesLatestFeignClient;

    /**
     * @param key     查询参数 - by ID, symbol, or slug
     * @param values  查询值
     * @param convert quote种类
     * @param aux     结果中需要的辅助数据
     * @return
     */
    @Override
    public List<QuotesLatestDto> getJson(String key, String values, String convert, String aux) {
        List<QuotesLatestDto> listQuotesLatestDto = new ArrayList<>();
        String strJson = getHttpJson(key, values, convert, aux);
        if (strJson == null || strJson.isEmpty()) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(strJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Status status = getStatus(rootNode);
        if (status == null || status.getErrorCode() != 0) {
            return null;
        }

        JsonNode data = rootNode.path("data");
        for (String id : values.split(",")) {
            JsonNode coin = data.path(id);
            QuotesLatestDto quotesLatestDto = new QuotesLatestDto();
            quotesLatestDto.setId(coin.get("id").asInt());
            if (coin.hasNonNull("name")) {
                quotesLatestDto.setName(coin.get("name").asText());
            }
            if (coin.hasNonNull("symbol")) {
                quotesLatestDto.setSymbol(coin.get("symbol").asText());
            }
            if (coin.hasNonNull("slug")) {
                quotesLatestDto.setSlug(coin.get("slug").asText());
            }
            if (coin.hasNonNull("num_market_pairs")) {
                quotesLatestDto.setNum_market_pairs(coin.get("num_market_pairs").asInt());
            }
            if (coin.hasNonNull("date_added")) {
                String date = DateHelper.utcToLocal(coin.get("date_added").asText());
                quotesLatestDto.setDate_added(date);
            }
            if (coin.hasNonNull("max_supply")) {
                quotesLatestDto.setMax_supply(coin.get("max_supply").asText());
            }
            if (coin.hasNonNull("circulating_supply")) {
                quotesLatestDto.setCirculating_supply(coin.get("circulating_supply").asText());
            }
            if (coin.hasNonNull("total_supply")) {
                quotesLatestDto.setTotal_supply(coin.get("total_supply").asText());
            }
            if (coin.hasNonNull("is_active")) {
                quotesLatestDto.setIs_active(coin.get("is_active").asInt());
            }
            if (coin.hasNonNull("is_fiat")) {
                quotesLatestDto.setIs_fiat(coin.get("is_fiat").asInt());
            }
            if (coin.hasNonNull("cmc_rank")) {
                quotesLatestDto.setCmc_rank(coin.get("cmc_rank").asInt());
            }
            if (coin.hasNonNull("last_updated")) {
                String date = DateHelper.utcToLocal(coin.get("last_updated").asText());
                quotesLatestDto.setLast_updated(date);
            }
            if (coin.hasNonNull("self_reported_circulating_supply")) {
                quotesLatestDto.setSelf_reported_circulating_supply(coin.get("self_reported_circulating_supply").asText());
            }
            if (coin.hasNonNull("self_reported_market_cap")) {
                quotesLatestDto.setSelf_reported_market_cap(coin.get("self_reported_market_cap").asText());
            }

            if (coin.hasNonNull("platform")) {
                Platform p = mapper.convertValue(coin.path("platform"), Platform.class);
                quotesLatestDto.setPlatform(p);
            }

            Quote quoteDto = getQuote(coin, convert);
            if(quoteDto != null){
                quotesLatestDto.setQuote(quoteDto);
            }

            List<String> listTags = getTags(coin);
            if (listTags != null) {
                quotesLatestDto.setTags(listTags);
            }

            listQuotesLatestDto.add(quotesLatestDto);
        }

        return listQuotesLatestDto;
    }

    private String getHttpJson(String key, String values, String convert, String aux) {
        String strJson = null;
        switch (key) {
            case "id":
                strJson = iQuotesLatestFeignClient.getHttpJsonById(values, convert,
                    "num_market_pairs,cmc_rank,date_added,platform,max_supply,circulating_supply,total_supply,is_active");
                break;
            case "symbol":
                strJson = iQuotesLatestFeignClient.getHttpJsonBySymbol(values, convert,
                    "num_market_pairs,cmc_rank,date_added,platform,max_supply,circulating_supply,total_supply,is_active");
                break;
            case "slug":
                strJson = iQuotesLatestFeignClient.getHttpJsonBySlug(values, convert,
                    "num_market_pairs,cmc_rank,date_added,platform,max_supply,circulating_supply,total_supply,is_active");
                break;
            default:
                strJson = null;
        }
        return strJson;
    }


    private Status getStatus(JsonNode rootNode) {
        Status status = null;
        if(rootNode.hasNonNull("status")){
            JsonNode jsonStatus = rootNode.path("status");
            try {
                ObjectMapper mapper = new ObjectMapper();
                status = mapper.readValue(jsonStatus.toString(), Status.class);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return status;
    }

    private Quote getQuote(JsonNode coin, String convert){
        Quote quote = null;
        if(coin.hasNonNull("quote")){
            JsonNode jsonQuote = coin.path("quote");
            if(jsonQuote.hasNonNull(convert)){
                JsonNode quoteConvert = jsonQuote.path(convert);
                ObjectMapper mapper = new ObjectMapper();
                quote = mapper.convertValue(quoteConvert, Quote.class);
                if (quote != null) {
                    quote.setQuote(convert);
                }
            }
        }
        return quote;
    }

    private List<String> getTags(JsonNode coin){
        List<String> listTags = null;
        if (coin.hasNonNull("tags")) {
            JsonNode tags = coin.path("tags");
            try {
                listTags =
                    new ObjectMapper().readValue(tags.traverse(), new TypeReference<ArrayList<String>>() {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return listTags;
    }

}