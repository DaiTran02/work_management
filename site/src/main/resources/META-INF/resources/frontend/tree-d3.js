import * as d3 from "https://d3js.org/d3.v6.min.js";

window.renderD3Tree = function (element) {
    const treeData = {
        name: "Ủy ban",
        children: [
            { name: "UB2" },
            { name: "UB1" },
            { 
                name: "Ủy ban 1",
                children: [
                    {
                        name: "Ủy ban 2",
                        children: [
                            { name: "MT1" },
                            { name: "MT1" },
                            { name: "MT1" },
                            { name: "MT1" }
                        ]
                    },
                    { name: "Ủy ban 2" },
                    { name: "Ủy ban 2" }
                ]
            }
        ]
    };

    // Clear the element
    element.innerHTML = "";

    // Set dimensions
    const width = 800, height = 600;

    const svg = d3.select(element)
        .append("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", "translate(50,50)");

    const root = d3.hierarchy(treeData);
    const treeLayout = d3.tree().size([width - 100, height - 200]);
    treeLayout(root);

    svg.selectAll("path.link")
        .data(root.links())
        .enter()
        .append("path")
        .attr("class", "link")
        .attr("fill", "none")
        .attr("stroke", "#555")
        .attr("stroke-width", 2)
        .attr("d", d3.linkVertical()
            .x(d => d.x)
            .y(d => d.y));

    const node = svg.selectAll("g.node")
        .data(root.descendants())
        .enter()
        .append("g")
        .attr("class", "node")
        .attr("transform", d => `translate(${d.x},${d.y})`);

    node.append("circle")
        .attr("r", 10)
        .attr("fill", "steelblue")
        .attr("stroke", "#fff")
        .attr("stroke-width", 2);

    node.append("text")
        .attr("dy", 4)
        .attr("y", d => d.children ? -20 : 20)
        .style("text-anchor", "middle")
        .text(d => d.data.name);
};
