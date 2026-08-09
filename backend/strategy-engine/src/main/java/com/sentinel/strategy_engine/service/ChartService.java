package com.sentinel.strategy_engine.service;

import com.sentinel.strategy_engine.dto.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChartService {

    public List<ChartCandle> build(

            List<CandleData> candles,

            StrategyRequest request

    ) {

        List<ChartCandle> result =

                new ArrayList<>();

        double emaFast = 0;

        double emaSlow = 0;

        for (

                int i = 0;

                i < candles.size();

                i++

        ) {

            CandleData c =

                    candles.get(i);

            double close =

                    Double.parseDouble(
                            c.getClose()
                    );

            if (

                    i == 0

            ) {

                emaFast =
                        close;

                emaSlow =
                        close;

            }

            else {

                emaFast =

                        (

                                close

                                        -

                                        emaFast

                        )

                                *

                                (

                                        2.0

                                                /

                                                (
                                                        request.getFast()
                                                                +
                                                                1
                                                )

                                )

                                +

                                emaFast;

                emaSlow =

                        (

                                close

                                        -

                                        emaSlow

                        )

                                *

                                (

                                        2.0

                                                /

                                                (
                                                        request.getSlow()
                                                                +
                                                                1
                                                )

                                )

                                +

                                emaSlow;

            }
            boolean signal =

                    emaFast
                            >
                            emaSlow;

            String event =

                    signal

                            ?

                            "EMA"

                            :

                            null;

            String signalType =

                    signal

                            ?

                            "BUY"

                            :

                            null;

            result.add(

                    new ChartCandle(

                            c.getDatetime(),

                            Double.parseDouble(
                                    c.getOpen()
                            ),

                            Double.parseDouble(
                                    c.getHigh()
                            ),

                            Double.parseDouble(
                                    c.getLow()
                            ),

                            close,

                            Double.parseDouble(
                                    c.getVolume()
                            ),

                            emaFast,

                            emaSlow,

                            signal,

                            event,

                            signalType

                    )

            );
        }

        return result;

    }

}