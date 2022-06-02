//
//            enterNode.append("text").attr("width", "500")
//                .append("textPath")
//                .attr("alignment-baseline", "top")
//                .attr("xlink:href", function (d) {
//                    return "#path-" + d.id;
//                })
//                .html(function (d) {
//                    d.name;
//                });

//            <text width="500">
//                <textPath alignment-baseline="top" xlink:href="#curve">
//                Dangerous Curves Ahead
//                </textPath>
//            </text>


//            node.on("mouseover", function (d, i) {
////                d3.select(this).select("circle").transition().duration(500)
////                    .attr("r", 70);
//
//                    var op = d3.select(this).style("opacity");
//
//                    d3.select(this).style("opacity", function(d) {
//                        if((d.selected == undefined || !d.selected) && (d.saved == undefined || !d.saved)) {
//                            d3.select(this).style("opacity", 0.2);
//                        }
//                    });
//                }
//            );
//
//            node.on("mouseout", function (d, i) {
////                d3.select(this).select("circle").transition().duration(500)
////                    .attr("r", 65);
//
//                var op = d3.select(this).style("opacity");
//
//                d3.select(this).style("opacity", function(d) {
//                    if((d.selected == undefined || !d.selected) && (d.saved == undefined || !d.saved)) {
//                        d3.select(this).style("opacity", 0.5);
//                    }
//                });
//            });