/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 * ================================================================================
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
 * 
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

/*
 * Create a chart and append it to a container
 */
function createChart(data, container, title, unit, lineStroke, nodeColour) {
    // Set the dimensions of the canvas
    var margin = {
        top : 30,
        right : 20,
        bottom : 30,
        left : 50
    }, width = 600 - margin.left - margin.right, height = 270 - margin.top
            - margin.bottom;

    // Set the ranges
    var x = d3.time.scale().range([ 0, width ]);
    var y = d3.scale.linear().range([ height, 0 ]);

    // Define the axes
    var xAxis = d3.svg.axis().scale(x).orient("bottom").ticks(5).innerTickSize(
            -height).outerTickSize(0).tickPadding(10);

    var yAxis = d3.svg.axis().scale(y).orient("left").ticks(10).innerTickSize(
            -width).outerTickSize(0).tickPadding(10);

    // Define the line
    var valueline = d3.svg.line().x(function(d) {
        return x(d.timestamp);
    }).y(function(d) {
        return y(d.value);
    });

    // Add the svg canvas to the container
    var svg = d3.select(container).append("svg").attr("preserveAspectRatio",
            "xMinYMin meet").attr("viewBox", "0 0 600 400").classed(
            "svg-content-responsive", true).append("g").attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

    // Set the unit for the value
    svg.attr("unit", unit);

    // Format the data for the chart
    data.forEach(function(d) {
        d.timestamp = d.timestamp;
        d.value = +d.value;
    });

    // Scale the range of the data
    x.domain(d3.extent(data, function(d) {
        return d.timestamp;
    }));
    y.domain([ 0, d3.max(data, function(d) {
        return Math.ceil((d.value + 1) / 10) * 10;
    }) ]);

    // Set the colour of the line
    if (!lineStroke) {
        lineStroke = "#5fbadd"
    }

    // Set the colour of the circles
    if (!nodeColour) {
        nodeColour = "#00A9D4"
    }

    // Add the valueline path
    svg.append("path").attr("class", "line").data(data).attr("unit", unit)
            .attr("stroke", lineStroke).attr("d", valueline(data));

    // Add the scatterplot
    svg.selectAll("circle").data(data).enter().append("circle").attr("r", 3.5)
            .attr("class", "circle").attr("fill", nodeColour).attr("cx",
                    function(d) {
                        return x(d.timestamp);
                    }).attr("cy", function(d) {
                return y(d.value);
            })

            // Apply the tooltip to each node
            .on(
                    "mouseover",
                    function(d) {
                        d3.select("body").select(".tooltip").transition()
                                .duration(50).style("opacity", 1);
                        d3.select("body").select(".tooltip").html(
                                formatDate(new Date(d.timestamp)) + "<br/>"
                                        + d.value + (unit ? " " + unit : ""))
                                .style("left", (d3.event.pageX) + "px").style(
                                        "top", (d3.event.pageY - 28) + "px");
                    }).on(
                    "mouseout",
                    function(d) {
                        d3.select("body").select(".tooltip").transition()
                                .duration(500).style("opacity", 0);
                    });

    // Add the X Axis
    svg.append("g").attr("class", "x axis").attr("transform",
            "translate(0," + height + ")").call(xAxis);

    // Add the Y Axis
    svg.append("g").attr("class", "y axis").call(yAxis);

    // Add the title
    svg.append("text").attr("x", (width / 2)).attr("y", 0 - (margin.top / 2))
            .attr("text-anchor", "middle").style("font-size", "16px").style(
                    "text-decoration", "underline").text(title);

    // Add the background
    svg.selectAll(".tick:not(:first-of-type) line").attr("stroke", "#777")
            .attr("stroke-dasharray", "2,2");
}

/*
 * Generates random chart data. Used when initializing the charts so that they
 * are not empty on load
 */
function generateRandomData() {
    var data = [];
    for (var i = 0; i < 30; i++) {
        data.push({
            timestamp : new Date().getTime() - (i * 5000),
            value : Math.floor(Math.random() * 100) + 1
        });
    }
    return data;
}

/*
 * Update a chart belonging to a specific container
 */
function updateChart(container, data, nodeColour) {
    var margin = {
        top : 30,
        right : 20,
        bottom : 30,
        left : 50
    }, width = 600 - margin.left - margin.right, height = 270 - margin.top
            - margin.bottom;
    var parseDate = d3.time.format("%d-%b-%y").parse;

    // Format the data for the chart
    data.forEach(function(d) {
        d.timestamp = d.timestamp;
        d.value = +d.value;
    });

    // Select the chart
    var svg = d3.select(container);

    // Set the ranges
    var x = d3.time.scale().range([ 0, width ]);
    var y = d3.scale.linear().range([ height, 0 ]);

    // Define the axes
    var xAxis = d3.svg.axis().scale(x).orient("bottom").ticks(5).innerTickSize(
            -height).outerTickSize(0).tickPadding(10);

    var yAxis = d3.svg.axis().scale(y).orient("left").ticks(10).innerTickSize(
            -width).outerTickSize(0).tickPadding(10);

    // Scale the range of the data
    x.domain(d3.extent(data, function(d) {
        return d.timestamp;
    }));
    y.domain([ 0, d3.max(data, function(d) {
        return Math.ceil((d.value + 1) / 10) * 10;
    }) ]);

    // Update the valueline path
    var valueline = d3.svg.line().x(function(d) {
        return x(d.timestamp);
    }).y(function(d) {
        return y(d.value);
    });

    var unit = svg.select(".line").attr("unit");

    // Remove all nodes
    svg.selectAll("circle").remove();

    // Set the node colour if one is passed in
    if (!nodeColour) {
        nodeColour = "#00A9D4"
    }

    // Make the changes
    svg.select(".line").data(data) // change the line
    .transition().duration(750).attr("d", valueline(data));
    svg.select(".x.axis") // change the x axis
    .transition().duration(750).call(xAxis.ticks(5));
    svg.select(".y.axis") // change the y axis
    .transition().duration(750).call(yAxis);

    // Redraw the nodes based on the new data
    svg.select("svg").select("g").selectAll("circle").data(data).enter()
            .append("circle").attr("r", 3.5).attr("class", "circle").attr(
                    "fill", nodeColour).attr("cx", function(d) {
                return x(d.timestamp);
            }).attr("cy", function(d) {
                return y(d.value);
            })

            // Apply the tooltip to each node
            .on(
                    "mouseover",
                    function(d) {
                        d3.select("body").select(".tooltip").transition()
                                .duration(50).style("opacity", 1);
                        d3.select("body").select(".tooltip").html(
                                formatDate(new Date(d.timestamp)) + "<br/>"
                                        + d.value + (unit ? " " + unit : ""))
                                .style("left", (d3.event.pageX) + "px").style(
                                        "top", (d3.event.pageY - 28) + "px");
                    }).on(
                    "mouseout",
                    function(d) {
                        d3.select("body").select(".tooltip").transition()
                                .duration(500).style("opacity", 0);
                    });

}

/*
 * Initialize a singleton div used as a floating tooltip for all charts
 */
function initTooltip() {
    d3.select("body").append("div").attr("class", "tooltip").attr("id",
            "tooltip").style("opacity", 0);
}

/*
 * Format a date object to string
 */
function formatDate(date) {
    return date.toLocaleString().replace(',', '');
}