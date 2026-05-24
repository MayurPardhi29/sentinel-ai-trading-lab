export function calculateEMA(
    data: any[],
    period: number
) {

    if (!data.length) {
        return [];
    }

    const multiplier =
        2 / (period + 1);

    let ema =
        Number(
            data[0].close
        );

    return data.map(
        (item) => {

            ema =
                (
                    Number(item.close)
                    - ema
                )
                *
                multiplier
                +
                ema;

            return {
                ...item,
                ema
            };

        }
    );

}