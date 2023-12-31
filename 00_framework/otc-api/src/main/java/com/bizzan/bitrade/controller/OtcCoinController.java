package com.bizzan.bitrade.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.service.OtcCoinService;
import com.bizzan.bitrade.util.MessageResult;

import static com.bizzan.bitrade.util.MessageResult.success;

import java.util.List;
import java.util.Map;

/**
 * @author Hevin  E-mail:bizzanhevin@gmail.com
 * @date 2020年01月06日
 */
@RestController
@Slf4j
@RequestMapping(value = "/coin")
public class OtcCoinController {

    @Autowired
    private OtcCoinService coinService;
    @Autowired
    private CoinExchangeFactory coins;

    /**
     * 取得正常的币种
     *
     * @return
     */
    @RequestMapping(value = "all")
    public MessageResult allCoin(@RequestParam(value = "currency", defaultValue = "CNY") String currency) throws Exception {
        List<Map<String, String>> list = coinService.getAllNormalCoin();
        list.stream().forEachOrdered(x ->{
            if(coins.get(x.get("unit"),currency) != null) {
                x.put("marketPrice", coins.get(x.get("unit"),currency).toString());
            }
        });
        MessageResult result = success();
        result.setData(list);
        return result;
    }
}
