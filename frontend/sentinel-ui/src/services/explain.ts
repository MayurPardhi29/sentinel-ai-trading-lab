export function explain(
    detected : boolean,
    confidence : number
){
    if( !detected ){
        return "No strong bullish signal";
    }

    if(confidence>80){
        return "Strong bullish momentum";
    }

    if(confidence> 50){
        return "Moderate bullish confirmation";
    }

    return "Weak bullish confirmation";
}