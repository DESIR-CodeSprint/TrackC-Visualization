
var app = angular.module('desirApp', []);

var dataobject;
var scene;
var camera;
var renderer;
var container3D;
var ambientLight;
var directionalLight;
var pointsObject;
var pointSpheres = [];
var lineObjects = [];
var raycaster;
var currentIntersectedPoint;
var currentIntersectedPointIndex;
var currentIntersectedLine;
var currentIntersectedLineIndex;
var mouse;
var hooverPointSphere;
var pointSize = 0.1;
var mouseText;

var nodeSelection;
var nodeTexts;
var nodeTypes;
var edgeTexts;
var segments;

window.onload = function () {
    if (!Detector.webgl)
        Detector.addGetWebGLMessage();

    container3D = document.getElementById('Container3D');

    var width = window.innerWidth - 340;
    var height = window.innerHeight;

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0x111111);
    scene.fog = new THREE.FogExp2(0x000, 0.003);
    camera = new THREE.PerspectiveCamera(25, width / height, 0.1, 1000);

    renderer = new THREE.WebGLRenderer({antialias: true});
    renderer.setSize(width, height);
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setClearColor(scene.fog.color);
    renderer.autoClear = true;

    // LIGHTS
    ambientLight = new THREE.AmbientLight(0x333333);
    scene.add(ambientLight);
    directionalLight = new THREE.DirectionalLight(0xAAAAAA, 1.0);
    scene.add(directionalLight);

    //CONTROLS
    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.addEventListener('change', render); // use only if there is no animation loop
    controls.enablePan = false;

    //INTERSECTION RAYCASTER
    raycaster = new THREE.Raycaster();

    //mouse coordinates
    mouse = new THREE.Vector2();
    container3D.addEventListener('mousemove', onDocumentMouseMove, false);
    //container3D.addEventListener('click', onDocumentMouseClick, false);

    //mouse text
    mouseText = document.createElement('div');
    mouseText.style.position = 'absolute';
    //mouseText.style.zIndex = 1;    // if you still don't see the label, try uncommenting this
    mouseText.style.width = 200;
    mouseText.style.height = 400;
    mouseText.style.color = "#ffffff";
    //mouseText.style.backgroundColor = "#000000";
    mouseText.style.paddingTop = 20 + 'px';
    mouseText.style.paddingBottom = 20 + 'px';
    mouseText.style.paddingRight = 20 + 'px';
    mouseText.style.paddingLeft = 20 + 'px';
    mouseText.innerHTML = "Node";
    mouseText.style.top = 0 + 'px';
    mouseText.style.left = 0 + 'px';
    mouseText.style.visibility = 'hidden';
    document.body.appendChild(mouseText);


    container3D.appendChild(renderer.domElement);

    updateCharts();

    render();
};

function updateCharts() {
    if (dataobject !== undefined) {
        document.getElementById("myChart1").style.display = 'block';
    } else {
        document.getElementById("myChart1").style.display = 'none';
    }
}

function onDocumentMouseClick(event) {
    if (currentIntersectedPointIndex < 0)
        return;

    if (nodeSelection[currentIntersectedPointIndex] === 0)
        nodeSelection[currentIntersectedPointIndex] = 1;
    else
        nodeSelection[currentIntersectedPointIndex] = 0;

    draw3D();
}


function onDocumentMouseMove(event) {
    event.preventDefault();

    let canvasBounds = renderer.context.canvas.getBoundingClientRect();
    mouse.x = ((event.clientX - canvasBounds.left) / (canvasBounds.right - canvasBounds.left)) * 2 - 1;
    mouse.y = -((event.clientY - canvasBounds.top) / (canvasBounds.bottom - canvasBounds.top)) * 2 + 1;

//    var mouseOver = false;
//    if(mouse.x >= -1 && mouse.x <= 1 && mouse.y >= -1 && mouse.y <= 1) //this doesn't seem precise as we are in the range grater than -1:1 ?? 
//        mouseOver = true;    
//    if(mouseOver)
//        mouseText.style.visibility='visible';
//    else
//        mouseText.style.visibility='hidden';

    mouseText.style.left = 20 + event.clientX + 'px';
    mouseText.style.top = event.clientY + 'px';

    var mousePos = document.getElementById("mouse_pos");
    mousePos.innerHTML = mouse.x + ", " + mouse.y;

    raycaster.setFromCamera(mouse, camera);

    var ok = false;
    if (pointsObject !== undefined) {
        // find intersections with points
        var intersects = raycaster.intersectObject(pointsObject);
        var pp;
        var imin;

        //nie koniecznie [0], pętla po wszystkich i min odległości
        if (intersects.length > 0) {
            //distance
            var geometry = pointsObject.geometry;
            var attributes = geometry.attributes;
            var coords = attributes.position.array;
            var minD = 2 * pointSize;
            //TBD check only N=?10 first points sorted by distance from camera
            for (var i = 0; i < intersects.length; i++) {
                var ip = intersects[i].point;
                var p = new THREE.Vector3(coords[3 * intersects[i].index], coords[3 * intersects[i].index + 1], coords[3 * intersects[i].index + 2]);
                var d = ip.distanceTo(p);
                if (d < minD) {
                    minD = d;
                    imin = i;
                    pp = p;
                }
            }
            if (minD < pointSize / 16)
                ok = true;
        }
        if (ok) {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLine.material.color = new THREE.Color(0x999999);
            }
            currentIntersectedLine = undefined;
            currentIntersectedLineIndex = -1;

            currentIntersectedPoint = intersects[imin].object;
            currentIntersectedPointIndex = intersects[imin].index;
            hooverPointSphere.position.copy(pp);
            hooverPointSphere.visible = true;

            if (nodeTexts !== undefined && currentIntersectedPointIndex >= 0 && currentIntersectedPointIndex < nodeTexts.length)
                mouseText.innerHTML = "" + currentIntersectedPointIndex + ": " + nodeTexts[currentIntersectedPointIndex];
            else
                mouseText.innerHTML = "no data";

            mouseText.style.visibility = 'visible';
        } else {
            currentIntersectedPoint = undefined;
            currentIntersectedPointIndex = -1;
            hooverPointSphere.visible = false;
            mouseText.style.visibility = 'hidden';
        }
    }

    // find intersections with lines
    if (!ok && lineObjects !== undefined) {
        intersects = raycaster.intersectObjects(lineObjects);
        if (intersects.length > 0) {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLine.material.color = new THREE.Color(0x999999);
                currentIntersectedLineIndex = -1;
            }
            currentIntersectedLine = intersects[ 0 ].object;
            for (var i = 0; i < lineObjects.length; i++) {
                if (lineObjects[i] === currentIntersectedLine) {
                    currentIntersectedLineIndex = i;
                    break;
                }
            }

            currentIntersectedLine.material.linewidth = 10;
            currentIntersectedLine.material.color = new THREE.Color(0x990000);
            if (edgeTexts !== undefined && currentIntersectedLineIndex >= 0 && currentIntersectedLineIndex < edgeTexts.length) {
                mouseText.innerHTML = "" + currentIntersectedLineIndex + ": " + edgeTexts[currentIntersectedLineIndex];
                //mouseText.innerHTML = "" + nodeTexts[segments[2 * currentIntersectedLineIndex]] + " <-> " + nodeTexts[segments[2 * currentIntersectedLineIndex + 1]] + "<br>" +
                //        "Common publications: " + edgeTexts[currentIntersectedLineIndex];
            } else {
                mouseText.innerHTML = "no data";
            }

            mouseText.style.visibility = 'visible';
        } else {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLine.material.color = new THREE.Color(0x999999);
            }
            currentIntersectedLine = undefined;
            mouseText.style.visibility = 'hidden';
        }
    }

    render();
}

function draw3D() {
    if (pointsObject !== undefined)
        scene.remove(pointsObject);
    if (lineObjects !== undefined) {
        for (var i = 0; i < lineObjects.length; i++)
            scene.remove(lineObjects[i]);
        lineObjects = [];
    }
    if (hooverPointSphere !== undefined)
        scene.remove(hooverPointSphere);
    if (pointSpheres !== undefined) {
        for (var i = 0; i < pointSpheres.length; i++)
            scene.remove(pointSpheres[i]);
        pointSpheres = [];
    }

    //get object data from scope
    var coords = dataobject.coords;
    segments = dataobject.segments;
    var nNodes = coords.length / 3;


    //create nodes geometry
    var pointsGeometry = new THREE.BufferGeometry();
    pointsGeometry.addAttribute('position', new THREE.Float32BufferAttribute(coords, 3));

    //get scene geometric extents
    pointsGeometry.computeBoundingSphere();
    pointsGeometry.computeBoundingBox();
    var bb = pointsGeometry.boundingBox;
    var bbcenter = bb.getCenter();
    var bbsize = bb.getSize();
    //set camera position to view the whole scene
    var maxDim = Math.max(bbsize.x, bbsize.y, bbsize.z);
    var fov = camera.fov * (Math.PI / 180);
    var cameraZ = Math.abs(maxDim / (2 * Math.tan(fov / 2))) * 2.0;
    camera.position.set(bbcenter.x, bbcenter.y, cameraZ);

    //create points at nodes of certain types
    //0=virtual, 10=actor_point, 11=actor_start, 12=actor_end, 20=event_point
    pointSize = 0.1 * maxDim;
    //var pointsMaterial = new THREE.PointsMaterial({color: 0x0000ff, size: pointSize});
    var pointsMaterial = new THREE.PointsMaterial({
        //vertexColors: true,
        size: 10 * pointSize,
        color: 0xffffff,
        map: new THREE.TextureLoader().load('img/xs-stars.png'), // '2.jpg' 
        transparent: true,
        side: THREE.DoubleSide,
        depthTest: false
    });

    //jak stworzyć różne typy materiału na punktach 
    pointsObject = new THREE.Points(pointsGeometry, pointsMaterial);
    scene.add(pointsObject);
    raycaster.linePrecision = pointSize / 8;

    //create sphere for point hoover
    var sphereGeometry = new THREE.SphereGeometry(pointSize / 16, 32, 32);
    var sphereMaterial = new THREE.MeshBasicMaterial({color: 0xff0000});
    hooverPointSphere = new THREE.Mesh(sphereGeometry, sphereMaterial);
    hooverPointSphere.visible = false;
    scene.add(hooverPointSphere);

    //create spheres for point selection
    for (var i = 0; i < nNodes; i++) {
        if (nodeSelection[i] === 0)
            continue;

        var sphereGeometry = new THREE.SphereGeometry(pointSize / 16, 32, 32);
        var sphereMaterial = new THREE.MeshBasicMaterial({color: 0x00ff00});
        pointSpheres[i] = new THREE.Mesh(sphereGeometry, sphereMaterial);
        pointSpheres[i].visible = true;
        pointSpheres[i].position.x = coords[3 * i];
        pointSpheres[i].position.y = coords[3 * i + 1];
        pointSpheres[i].position.z = coords[3 * i + 2];
        scene.add(pointSpheres[i]);
    }

    //create segments geometry and lines
    var nSegments = segments.length / 2;
    var a, b;
    for (var i = 0; i < nSegments; i++) {
        var linesGeometry = new THREE.Geometry();
        a = segments[2 * i];
        b = segments[2 * i + 1];
        linesGeometry.vertices.push(new THREE.Vector3(coords[3 * a], coords[3 * a + 1], coords[3 * a + 2]));
        linesGeometry.vertices.push(new THREE.Vector3(coords[3 * b], coords[3 * b + 1], coords[3 * b + 2]));
        //var linesMaterial = new THREE.LineBasicMaterial({color: 0xff0000, linewidth: 1});
        var linesMaterial = new THREE.LineBasicMaterial({
            color: 0x999999,
            linewidth: 1,
            opacity: 0.7,
            blending: THREE.AdditiveBlending,
            transparent: true
        });

        var lineObject = new THREE.LineSegments(linesGeometry, linesMaterial);
        lineObjects.push(lineObject);
        scene.add(lineObject);
    }

    //barcharts();

    render();
}

function render() {
    //set light position at camera position
    directionalLight.position.set(camera.position.x, camera.position.y, camera.position.z).normalize();
    //do rendering
    renderer.render(scene, camera);
}

function barcharts() {
    var filteredData = crossfilter(dataobject);
    var all = filteredData.groupAll();
    var fdata = filteredData.dimension(function(d) { return  d.segmentFData; });
    var fdatas = fdata.group(function(d) { return Math.floor(d); });

    
    var charts = [barChart()
                .dimension(fdata)
                .group(fdatas)
                .x(d3.scale.linear()
                        .domain([0, 100])
                        .rangeRound([0, 300]))
    ];

    var chart = d3.selectAll(".chart")
            .data(charts)
            .each(function (chart) {
                chart.on("brush", renderAll).on("brushend", renderAll);
            });
    renderAll();


    // Renders the specified chart or list.
    function render(method) {
        d3.select(this).call(method);
    }

    // Whenever the brush moves, re-rendering everything.
    function renderAll() {
        chart.each(render);

        throttle(passValues);
    }


    function barChart() {
        if (!barChart.id)
            barChart.id = 0;

        var margin = {top: 10, right: 10, bottom: 20, left: 10},
                x,
                y = d3.scale.linear().range([50, 0]),
                id = barChart.id++,
                axis = d3.svg.axis().orient("bottom"),
                brush = d3.svg.brush(),
                brushDirty,
                dimension,
                group,
                round;

        function chart(div) {
            var width = x.range()[1],
                    height = y.range()[0];

            y.domain([0, group.top(1)[0].value]);

            div.each(function () {
                var div = d3.select(this),
                        g = div.select("g");

                // Create the skeletal chart.
                if (g.empty()) {
                    div.select(".title").append("a")
                            .attr("href", "javascript:reset(" + id + ")")
                            .attr("class", "reset")
                            .text("reset")
                            .style("display", "none");

                    g = div.append("svg")
                            .attr("width", width + margin.left + margin.right)
                            .attr("height", height + margin.top + margin.bottom)
                            .append("g")
                            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    g.append("clipPath")
                            .attr("id", "clip-" + id)
                            .append("rect")
                            .attr("width", width)
                            .attr("height", height);

                    g.selectAll(".bar")
                            .data(["background", "foreground"])
                            .enter().append("path")
                            .attr("class", function (d) {
                                return d + " bar";
                            })
                            .datum(group.all());

                    g.selectAll(".foreground.bar")
                            .attr("clip-path", "url(#clip-" + id + ")");

                    g.append("g")
                            .attr("class", "axis")
                            .attr("transform", "translate(0," + height + ")")
                            .call(axis);

                    // Initialize the brush component with pretty resize handles.
                    var gBrush = g.append("g").attr("class", "brush").call(brush);
                    gBrush.selectAll("rect").attr("height", height);
                    gBrush.selectAll(".resize").append("path").attr("d", resizePath);
                }

                // Only redraw the brush if set externally.
                if (brushDirty) {
                    brushDirty = false;
                    g.selectAll(".brush").call(brush);
                    div.select(".title a").style("display", brush.empty() ? "none" : null);
                    if (brush.empty()) {
                        g.selectAll("#clip-" + id + " rect")
                                .attr("x", 0)
                                .attr("width", width);
                    } else {
                        var extent = brush.extent();
                        g.selectAll("#clip-" + id + " rect")
                                .attr("x", x(extent[0]))
                                .attr("width", x(extent[1]) - x(extent[0]));
                    }
                }

                g.selectAll(".bar").attr("d", barPath);
            });

            function barPath(groups) {
                var path = [],
                        i = -1,
                        n = groups.length,
                        d;
                while (++i < n) {
                    d = groups[i];
                    path.push("M", x(d.key), ",", height, "V", y(d.value), "h4V", height);
                }
                return path.join("");
            }

            function resizePath(d) {
                var e = +(d == "e"),
                        x = e ? 1 : -1,
                        y = height / 3;
                return "M" + (.5 * x) + "," + y
                        + "A6,6 0 0 " + e + " " + (6.5 * x) + "," + (y + 6)
                        + "V" + (2 * y - 6)
                        + "A6,6 0 0 " + e + " " + (.5 * x) + "," + (2 * y);
            }
        }

        brush.on("brushstart.chart", function () {
            var div = d3.select(this.parentNode.parentNode.parentNode);
            div.select(".title a").style("display", null);
        });

        brush.on("brush.chart", function () {
            var g = d3.select(this.parentNode),
                    extent = brush.extent();
            if (round)
                g.select(".brush")
                        .call(brush.extent(extent = extent.map(round)))
                        .selectAll(".resize")
                        .style("display", null);
            g.select("#clip-" + id + " rect")
                    .attr("x", x(extent[0]))
                    .attr("width", x(extent[1]) - x(extent[0]));
            dimension.filterRange(extent);
        });

        brush.on("brushend.chart", function () {
            if (brush.empty()) {
                var div = d3.select(this.parentNode.parentNode.parentNode);
                div.select(".title a").style("display", "none");
                div.select("#clip-" + id + " rect").attr("x", null).attr("width", "100%");
                dimension.filterAll();
            }
        });

        chart.margin = function (_) {
            if (!arguments.length)
                return margin;
            margin = _;
            return chart;
        };

        chart.x = function (_) {
            if (!arguments.length)
                return x;
            x = _;
            axis.scale(x);
            brush.x(x);
            return chart;
        };

        chart.y = function (_) {
            if (!arguments.length)
                return y;
            y = _;
            return chart;
        };

        chart.dimension = function (_) {
            if (!arguments.length)
                return dimension;
            dimension = _;
            return chart;
        };

        chart.filter = function (_) {
            if (_) {
                brush.extent(_);
                dimension.filterRange(_);
            } else {
                brush.clear();
                dimension.filterAll();
            }
            brushDirty = true;
            return chart;
        };

        chart.group = function (_) {
            if (!arguments.length)
                return group;
            group = _;
            return chart;
        };

        chart.round = function (_) {
            if (!arguments.length)
                return round;
            round = _;
            return chart;
        };

        return d3.rebind(chart, brush, "on");
    }


}

function processDataObject(data) {
    dataobject = data;
    nodeTexts = dataobject.nodeData;
    nodeTypes = dataobject.nodeTypeData;
    edgeTexts = dataobject.segmentData;
    nodeSelection = new Uint8Array(nodeTexts.length);
    for (var i = 0; i < nodeTexts.length; i++) {
        nodeSelection[i] = 0;
    }

    updateCharts();
    draw3D();
    render();   
}

app.controller('RetrieveMyObjectController', ['$scope', '$http', '$q', '$location', function ($scope, $http, $q, $location) {
        $scope.retrieveMyObject = function () {
            var ID = $scope.dataobject.id;
            var text = $scope.dataobject.text;
            var url = $location.host()
            var server = "";
            if (url === "") {
            	server = "http://localhost:8080/"
            }
            $http.get(server + 'dataobject?id=' + ID + '&text=' + text).
                    then(function (response) {
                        $scope.dataobject = response.data;
                        if ($scope.dataobject.text === "undefined")
                            $scope.dataobject.text = "";

                        //var showID = document.getElementById("id_display");
                        //showID.innerHTML = $scope.dataobject.id;

                        //var showSEQ = document.getElementById("seq_display");
                        //showSEQ.innerHTML = ";  Text: " + $scope.dataobject.text;

                        processDataObject($scope.dataobject);
                        
                    });
        };

        $scope.retrieveBibsonomyQuery = function () {
            var login = $scope.dataobject.login;
            var apikey = $scope.dataobject.apikey;
            var refUsername = $scope.dataobject.refUsername;
            var tags = $scope.dataobject.tags;
            var text = $scope.dataobject.text;
            var url = $location.host()
            var server = "";
            if (url === "") {
            	server = "http://localhost:8080/"
            }
            $http.get(server + 'retrieveBibsonomyQuery?login=' + login + '&text=' + text + '&apikey=' + apikey + '&refUsername=' + refUsername + '&tags=' + tags).
                    then(function (response) {
                        $scope.dataobject = response.data;
                        if ($scope.dataobject.text === "undefined")
                            $scope.dataobject.text = "";

                        //var showID = document.getElementById("id_display");
                        //showID.innerHTML = $scope.dataobject.id;

                        //var showSEQ = document.getElementById("seq_display");
                        //showSEQ.innerHTML = ";  Text: " + $scope.dataobject.text;

                        processDataObject($scope.dataobject);
                    });
        };

    }]);
