
var app = angular.module('desirApp', []);
var dataobject;
var scene;
var camera;
var renderer;
var container3D;
var ambientLight;
var directionalLight;
var actorPointsObject;
var actorLinesObjects = [];
var actorPointsGeometry;
var eventPointsObject;
var eventLinesObjects = [];
var eventPointsGeometry;
var participLinesObjects = [];
var raycaster;
var currentIntersectedPoint;
var currentIntersectedPointIndex;
var lastIntersectedPoint;
var lastIntersectedPointIndex;
var currentIntersectedLine;
var currentIntersectedLineIndex;
var mouse;
var pointSize = 0.1;
var mouseText;


var coords;
var nNodes = 0;
var actorNodeIndices;
var eventNodeIndices;
var nodeDataIDs;
var segments;
var actorSegmentIndices;
var eventSegmentIndices;
var participSegmentIndices;
var segmentDataIDs;
var quads;
var quadDataIDs;
var data;
var nodeSelection;
var nodeSizes;
var nodeColors;

//variables for object visibility
var visibleActors;
var visibleEvents;
var visibleRelationsParticipation;
var visibleRelationsDependency;

//variables for global colors
var cActorDefaultColor;
var cActorHoverColor;
var cActorSelectedColor;

var cEventDefaultColor;
var cEventHoverColor;
var cEventSelectedColor;

var cParticipDefaultColor;
var cParticipHoverColor;
var cParticipSelectedColor;


window.onload = function () {
    if (!Detector.webgl)
        Detector.addGetWebGLMessage();

    container3D = document.getElementById('Container3D');

    setupColors();
    visibleActors = true;
    visibleEvents = true;
    visibleRelationsParticipation = true;
    visibleRelationsDependency = false;

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
    //mouse events
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
    //animate();
};

function updateCharts() {
    if (dataobject !== undefined) {
        document.getElementById("myChart1").style.display = 'block';
    } else {
        document.getElementById("myChart1").style.display = 'none';
    }
}

function onDocumentMouseClick(event) {
    //TBD fix click event - press+drag+release should not be a click for point selection?
    if (currentIntersectedPointIndex < 0)
        return;
    if (nodeSelection === null || nodeSelection === undefined)
        return;

    var color;
    if (nodeSelection[currentIntersectedPointIndex] === 0) {
        nodeSelection[currentIntersectedPointIndex] = 1;
        color = cActorSelectedColor;
    } else {
        nodeSelection[currentIntersectedPointIndex] = 0;
        color = cActorDefaultColor;
    }

    color.toArray(actorPointsGeometry.attributes.ca.array, currentIntersectedPointIndex * 3);
    actorPointsGeometry.attributes.ca.needsUpdate = true;

    color.toArray(eventPointsGeometry.attributes.ca.array, currentIntersectedPointIndex * 3);
    eventPointsGeometry.attributes.ca.needsUpdate = true;

    render();
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

    if (currentIntersectedPointIndex >= 0) {
        lastIntersectedPoint = currentIntersectedPoint;
        lastIntersectedPointIndex = currentIntersectedPointIndex;
    }
    
    
    //TODO
    //rewrite intersectioning to use objectID and highlight all geometries with this ID 
    //keep currentDataID instead of pointIndex
    //highlight all geometries with dataIDs == currentDataID
    //rewrite line appearance change utilizing BufferedGeometry and shaders, add line trasparency, etc.
    //how shall we represent time in 3D view? color? axis?
    //solve point scale problem? add UI for changing
    //enable choosing subcomponents on optimized location grid

    //====================== find intersections with actor points ========================
    var intersectionFound = false;
    if (actorPointsObject !== undefined) {
        var intersects = raycaster.intersectObject(actorPointsObject);
        var pp;
        var imin = 0;

        //nie koniecznie [0], pętla po wszystkich i min odległości
        if (intersects.length > 0) {
            //distance
            var geometry = actorPointsObject.geometry;
            var attributes = geometry.attributes;
            var coords = attributes.position.array;
            var minD = 2 * pointSize;
            //TBD check only N=?10 first points sorted by distance from camera
            for (var i = 0; i < intersects.length; i++) {
                var ip = intersects[i].point;
                var ipi = intersects[i].index;
                var p = new THREE.Vector3(coords[3 * ipi], coords[3 * ipi + 1], coords[3 * ipi + 2]);
                var d = ip.distanceTo(p);
                if (d < minD) {
                    minD = d;
                    imin = i;
                    pp = p;
                }
            }
            if (minD < pointSize / 16)
                intersectionFound = true;
        }
        if (intersectionFound) { //this is what happens when a node is pointed with a mouse
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLine.material.color = new THREE.Color(0x999999);
            }
            currentIntersectedLine = undefined;
            currentIntersectedLineIndex = -1;

            currentIntersectedPoint = intersects[imin].object;
            currentIntersectedPointIndex = intersects[imin].index;

            cActorHoverColor.toArray(actorPointsGeometry.attributes.ca.array, currentIntersectedPointIndex * 3);
            actorPointsGeometry.attributes.ca.needsUpdate = true;

            if (nodeDataIDs !== undefined && currentIntersectedPointIndex >= 0 && currentIntersectedPointIndex < nodeDataIDs.length) {
                mouseText.innerHTML = "" + nodeDataIDs[currentIntersectedPointIndex];

//                if (nodeMetadata !== undefined && nodeMetadata !== null) {
//                    var md = nodeMetadata[currentIntersectedPointIndex];
//                    if (md !== null && md !== "") {
//                        var mdMap = new Map(Object.entries(JSON.parse(md)));
//                        for (let [k, v] of mdMap) {
//                            mouseText.innerHTML = mouseText.innerHTML + "<br>" + k + "=" + v;
//                        }
//                    }
//                }

                //mouseText.innerHTML = "" + currentIntersectedPointIndex + ": " + nodeTexts[currentIntersectedPointIndex];
            } else {
                mouseText.innerHTML = "no data";
            }

            mouseText.style.visibility = 'visible';
        } else {
            currentIntersectedPoint = undefined;
            currentIntersectedPointIndex = -1;
            if (lastIntersectedPointIndex >= 0 && isActorNode(lastIntersectedPointIndex)) {
                var color;
                if (nodeSelection[lastIntersectedPointIndex] === 0) {
                    color = cActorDefaultColor;
                } else {
                    color = cActorSelectedColor;
                }
                color.toArray(actorPointsGeometry.attributes.ca.array, lastIntersectedPointIndex * 3);
                actorPointsGeometry.attributes.ca.needsUpdate = true;
            }
            mouseText.style.visibility = 'hidden';
        }
    }
    
    //====================== find intersections with events points ========================
    if (!intersectionFound && eventPointsObject !== undefined) {
        // find intersections with event points
        var intersects = raycaster.intersectObject(eventPointsObject);
        var pp;
        var imin = 0;

        //nie koniecznie [0], pętla po wszystkich i min odległości
        if (intersects.length > 0) {
            //distance
            var geometry = eventPointsObject.geometry;
            var attributes = geometry.attributes;
            var coords = attributes.position.array;
            var minD = 2 * pointSize;
            //TBD check only N=?10 first points sorted by distance from camera
            for (var i = 0; i < intersects.length; i++) {
                var ip = intersects[i].point;
                var ipi = intersects[i].index;
                var p = new THREE.Vector3(coords[3 * ipi], coords[3 * ipi + 1], coords[3 * ipi + 2]);
                var d = ip.distanceTo(p);
                if (d < minD) {
                    minD = d;
                    imin = i;
                    pp = p;
                }
            }
            if (minD < pointSize / 16)
                intersectionFound = true;
        }
        if (intersectionFound) { //this is what happens when a node is pointed with a mouse
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLine.material.color = new THREE.Color(0x999999);
            }
            currentIntersectedLine = undefined;
            currentIntersectedLineIndex = -1;

            currentIntersectedPoint = intersects[imin].object;
            currentIntersectedPointIndex = intersects[imin].index;

            cEventHoverColor.toArray(eventPointsGeometry.attributes.ca.array, currentIntersectedPointIndex * 3);
            eventPointsGeometry.attributes.ca.needsUpdate = true;

            if (nodeDataIDs !== undefined && currentIntersectedPointIndex >= 0 && currentIntersectedPointIndex < nodeDataIDs.length) {
                mouseText.innerHTML = "" + nodeDataIDs[currentIntersectedPointIndex];

//                if (nodeMetadata !== undefined && nodeMetadata !== null) {
//                    var md = nodeMetadata[currentIntersectedPointIndex];
//                    if (md !== null && md !== "") {
//                        var mdMap = new Map(Object.entries(JSON.parse(md)));
//                        for (let [k, v] of mdMap) {
//                            mouseText.innerHTML = mouseText.innerHTML + "<br>" + k + "=" + v;
//                        }
//                    }
//                }

                //mouseText.innerHTML = "" + currentIntersectedPointIndex + ": " + nodeTexts[currentIntersectedPointIndex];
            } else {
                mouseText.innerHTML = "no data";
            }

            mouseText.style.visibility = 'visible';
        } else {
            currentIntersectedPoint = undefined;
            currentIntersectedPointIndex = -1;
            if (lastIntersectedPointIndex >= 0 && isEventNode(lastIntersectedPointIndex)) {
                var color;
                if (nodeSelection[lastIntersectedPointIndex] === 0) {
                    color = cEventDefaultColor;
                } else {
                    color = cEventSelectedColor;
                }
                color.toArray(eventPointsGeometry.attributes.ca.array, lastIntersectedPointIndex * 3);
                eventPointsGeometry.attributes.ca.needsUpdate = true;
            }
            mouseText.style.visibility = 'hidden';
        }
    }    

    //====================== find intersections with actor lines ========================
    if (!intersectionFound && actorLinesObjects !== undefined) {
        if (currentIntersectedLine !== undefined) {
            currentIntersectedLine.material.linewidth = 1;
            currentIntersectedLine.material.color = new THREE.Color(0x999999);
        }
        intersects = raycaster.intersectObjects(actorLinesObjects);
        if (intersects.length > 0) {
            currentIntersectedLine = intersects[ 0 ].object;
            for (var i = 0; i < actorLinesObjects.length; i++) {
                if (actorLinesObjects[i] === currentIntersectedLine) {
                    currentIntersectedLineIndex = actorSegmentIndices[i];           
                    break;
                }
            }
            intersectionFound = true;
        }
        if(intersectionFound) {
            currentIntersectedLine.material.linewidth = 10;
            currentIntersectedLine.material.color = new THREE.Color(0x990000);
            if (segmentDataIDs !== undefined && currentIntersectedLineIndex >= 0 && currentIntersectedLineIndex < segmentDataIDs.length) {
                mouseText.innerHTML = "" + segmentDataIDs[currentIntersectedLineIndex];

//                if (edgeMetadata !== undefined && edgeMetadata !== null) {
//                    var md = edgeMetadata[currentIntersectedLineIndex];
//                    if (md !== null && md !== "") {
//                        var mdMap = new Map(Object.entries(JSON.parse(md)));
//                        for (let [k, v] of mdMap) {
//                            mouseText.innerHTML = mouseText.innerHTML + "<br>" + k + "=" + v;
//                        }
//                    }
//                }

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
            currentIntersectedLineIndex = -1;
            mouseText.style.visibility = 'hidden';
        }
    }

    //====================== find intersections with event lines ========================
    if (!intersectionFound && eventLinesObjects !== undefined) {
        if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
                currentIntersectedLine.material.color = new THREE.Color(0x999999);
        }
        intersects = raycaster.intersectObjects(eventLinesObjects);
        if (intersects.length > 0) {
            currentIntersectedLine = intersects[ 0 ].object;
            for (var i = 0; i < eventLinesObjects.length; i++) {
                if (eventLinesObjects[i] === currentIntersectedLine) {
                    currentIntersectedLineIndex = eventSegmentIndices[i];           
                    break;
                }
            }
            intersectionFound = true;
        }
        if(intersectionFound) {
            currentIntersectedLine.material.linewidth = 10;
            currentIntersectedLine.material.color = new THREE.Color(0x990000);
            if (segmentDataIDs !== undefined && currentIntersectedLineIndex >= 0 && currentIntersectedLineIndex < segmentDataIDs.length) {
                mouseText.innerHTML = "" + segmentDataIDs[currentIntersectedLineIndex];

//                if (edgeMetadata !== undefined && edgeMetadata !== null) {
//                    var md = edgeMetadata[currentIntersectedLineIndex];
//                    if (md !== null && md !== "") {
//                        var mdMap = new Map(Object.entries(JSON.parse(md)));
//                        for (let [k, v] of mdMap) {
//                            mouseText.innerHTML = mouseText.innerHTML + "<br>" + k + "=" + v;
//                        }
//                    }
//                }

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
            currentIntersectedLineIndex = -1;
            mouseText.style.visibility = 'hidden';
        }
    }

// so far we have no data on participation segments so commented out    
//    //====================== find intersections with participation lines ========================
//    if (!intersectionFound && participLinesObjects !== undefined) {
//        intersects = raycaster.intersectObjects(participLinesObjects);
//        if (intersects.length > 0) {
//            currentIntersectedLine = intersects[ 0 ].object;
//            for (var i = 0; i < participLinesObjects.length; i++) {
//                if (participLinesObjects[i] === currentIntersectedLine) {
//                    currentIntersectedLineIndex = participSegmentIndices[i];           
//                    break;
//                }
//            }
//            intersectionFound = true;
//        }
//        if(intersectionFound) {
//            currentIntersectedLine.material.linewidth = 10;
//            currentIntersectedLine.material.color = new THREE.Color(0x990000);
//            if (segmentDataIDs !== undefined && currentIntersectedLineIndex >= 0 && currentIntersectedLineIndex < segmentDataIDs.length) {
//                mouseText.innerHTML = "" + segmentDataIDs[currentIntersectedLineIndex];
//
////                if (edgeMetadata !== undefined && edgeMetadata !== null) {
////                    var md = edgeMetadata[currentIntersectedLineIndex];
////                    if (md !== null && md !== "") {
////                        var mdMap = new Map(Object.entries(JSON.parse(md)));
////                        for (let [k, v] of mdMap) {
////                            mouseText.innerHTML = mouseText.innerHTML + "<br>" + k + "=" + v;
////                        }
////                    }
////                }
//
//            } else {
//                mouseText.innerHTML = "no data";
//            }
//
//            mouseText.style.visibility = 'visible';
//        } else {
//            if (currentIntersectedLine !== undefined) {
//                currentIntersectedLine.material.linewidth = 1;
//                currentIntersectedLine.material.color = new THREE.Color(0x999999);
//            }
//            currentIntersectedLine = undefined;
//            currentIntersectedLineIndex = -1;
//            mouseText.style.visibility = 'hidden';
//        }
//    }    
    
    render();
}

function draw3D() {
    //cleanup scene
    if (actorPointsObject !== undefined)
        scene.remove(actorPointsObject);
    if (actorLinesObjects !== undefined) {
        for (var i = 0; i < actorLinesObjects.length; i++)
            scene.remove(actorLinesObjects[i]);
        actorLinesObjects = [];
    }
    if (eventPointsObject !== undefined)
        scene.remove(eventPointsObject);
    if (eventLinesObjects !== undefined) {
        for (var i = 0; i < eventLinesObjects.length; i++)
            scene.remove(eventLinesObjects[i]);
        eventLinesObjects = [];
    }
    if (participLinesObjects !== undefined) {
        for (var i = 0; i < participLinesObjects.length; i++)
            scene.remove(participLinesObjects[i]);
        participLinesObjects = [];
    }
    
    
    
    if (visibleActors) {
        createActorsGeometry();
    }
    
    if(visibleEvents) {
        createEventsGeometry();
    }
    
    if(visibleRelationsParticipation) {
        createRelationsParticipationGeometry();
    }

    if(visibleRelationsDependency) {
        createRelationsDependencyGeometry();
    }
    
    

    //barcharts();
    render();
    doSceneSetup();
}

function createActorsGeometry() {
    if(nNodes < 1)
        return;
    if(coords === null || coords === undefined)
        return;
    if(nodeSizes === null || nodeSizes === undefined)
        return;
    if(nodeColors === null || nodeColors === undefined)
        return;
    
    if(actorNodeIndices !== null && actorNodeIndices !== undefined) {
        //create indexed nodes geometry
        actorPointsGeometry = new THREE.BufferGeometry();
        actorPointsGeometry.addAttribute('position', new THREE.Float32BufferAttribute(coords, 3)); 
        actorPointsGeometry.addAttribute('size', new THREE.BufferAttribute(nodeSizes, 1));
        actorPointsGeometry.addAttribute('ca', new THREE.BufferAttribute(nodeColors, 3));
        actorPointsGeometry.setIndex(actorNodeIndices);

        //create actor node appearance using texture
        var texture = new THREE.TextureLoader().load("img/ball.png"); //('img/xs-stars.png');
        texture.wrapS = THREE.RepeatWrapping;
        texture.wrapT = THREE.RepeatWrapping;
        var actorPointsMaterial = new THREE.ShaderMaterial({
            uniforms: {
                color: {value: new THREE.Color(0xffffff)},
                pointTexture: {value: texture}
            },
            vertexShader: document.getElementById('vertexshader').textContent,
            fragmentShader: document.getElementById('fragmentshader').textContent,
            //transparent: false,
            //side: THREE.DoubleSide,
            //depthTest: true
            //why lines are drawn over node texture?
            depthWrite: true
        });

        //create actor nodes object
        actorPointsObject = new THREE.Points(actorPointsGeometry, actorPointsMaterial);
        scene.add(actorPointsObject);
    }

    if(actorSegmentIndices !== null && actorSegmentIndices !== undefined) {
        //create actor segments geometry and lines
        var nSegments = actorSegmentIndices.length;
        var a, b;
        for (var i = 0; i < nSegments; i++) {
            var actorLinesGeometry = new THREE.Geometry();
            a = segments[2 * actorSegmentIndices[i]];
            b = segments[2 * actorSegmentIndices[i] + 1];
            actorLinesGeometry.vertices.push(new THREE.Vector3(coords[3 * a], coords[3 * a + 1], coords[3 * a + 2]));
            actorLinesGeometry.vertices.push(new THREE.Vector3(coords[3 * b], coords[3 * b + 1], coords[3 * b + 2]));
            var actorLinesMaterial = new THREE.LineBasicMaterial({
                color: 0x999999,
                linewidth: 1,
                opacity: 0.7,
                blending: THREE.AdditiveBlending,
                transparent: true,
                //depthWrite: true
            });

            var actorLineObject = new THREE.LineSegments(actorLinesGeometry, actorLinesMaterial);
            actorLinesObjects.push(actorLineObject);
            scene.add(actorLineObject);
        }
    }
    
}

function createEventsGeometry() {
    if(nNodes < 1)
        return;
    if(coords === null || coords === undefined)
        return;
    if(nodeSizes === null || nodeSizes === undefined)
        return;
    if(nodeColors === null || nodeColors === undefined)
        return;
    
    if(eventNodeIndices !== null && eventNodeIndices !== undefined) {
        //create indexed nodes geometry
        eventPointsGeometry = new THREE.BufferGeometry();
        eventPointsGeometry.addAttribute('position', new THREE.Float32BufferAttribute(coords, 3)); 
        eventPointsGeometry.addAttribute('size', new THREE.BufferAttribute(nodeSizes, 1));
        eventPointsGeometry.addAttribute('ca', new THREE.BufferAttribute(nodeColors, 3));
        eventPointsGeometry.setIndex(eventNodeIndices);


        //create event node appearance using texture
        var texture = new THREE.TextureLoader().load("img/ball.png"); //('img/xs-stars.png');
        texture.wrapS = THREE.RepeatWrapping;
        texture.wrapT = THREE.RepeatWrapping;
        var eventPointsMaterial = new THREE.ShaderMaterial({
            uniforms: {
                color: {value: new THREE.Color(0xffffff)},
                pointTexture: {value: texture}
            },
            vertexShader: document.getElementById('vertexshader').textContent,
            fragmentShader: document.getElementById('fragmentshader').textContent,
            //transparent: false,
            //side: THREE.DoubleSide,
            //depthTest: true
            //why lines are drawn over node texture?
            depthWrite: true
        });

        //create event nodes object
        eventPointsObject = new THREE.Points(eventPointsGeometry, eventPointsMaterial);
        scene.add(eventPointsObject);
    }
    
    if(eventSegmentIndices !== null && eventSegmentIndices !== undefined) {
        //create event segments geometry and lines
        var nSegments = eventSegmentIndices.length;
        var a, b;
        for (var i = 0; i < nSegments; i++) {
            var eventLinesGeometry = new THREE.Geometry();
            a = segments[2 * eventSegmentIndices[i]];
            b = segments[2 * eventSegmentIndices[i] + 1];
            eventLinesGeometry.vertices.push(new THREE.Vector3(coords[3 * a], coords[3 * a + 1], coords[3 * a + 2]));
            eventLinesGeometry.vertices.push(new THREE.Vector3(coords[3 * b], coords[3 * b + 1], coords[3 * b + 2]));
            var eventLinesMaterial = new THREE.LineBasicMaterial({
                color: 0x999999,
                linewidth: 1,
                opacity: 0.7,
                blending: THREE.AdditiveBlending,
                transparent: true,
                //depthWrite: true
            });

            var eventLineObject = new THREE.LineSegments(eventLinesGeometry, eventLinesMaterial);
            eventLinesObjects.push(eventLineObject);
            scene.add(eventLineObject);
        }
    }
    
}

function createRelationsParticipationGeometry() {
    if(nNodes < 2)
        return;
    if(coords === null || coords === undefined)
        return;
    
    //momentary participation - segments
    if(participSegmentIndices !== null && participSegmentIndices !== undefined) {
        //create participation segments geometry and lines
        var nSegments = participSegmentIndices.length;
        var a, b;
        for (var i = 0; i < nSegments; i++) {
            var participLinesGeometry = new THREE.Geometry();
            a = segments[2 * participSegmentIndices[i]];
            b = segments[2 * participSegmentIndices[i] + 1];
            participLinesGeometry.vertices.push(new THREE.Vector3(coords[3 * a], coords[3 * a + 1], coords[3 * a + 2]));
            participLinesGeometry.vertices.push(new THREE.Vector3(coords[3 * b], coords[3 * b + 1], coords[3 * b + 2]));
            var participLinesMaterial = new THREE.LineBasicMaterial({
                color: 0x999999,
                linewidth: 1,
                opacity: 0.7,
                blending: THREE.AdditiveBlending,
                transparent: true,
                //depthWrite: true
            });

            var participLineObject = new THREE.LineSegments(participLinesGeometry, participLinesMaterial);
            participLinesObjects.push(participLineObject);
            scene.add(participLineObject);
        }        
    }  
    
    //lasting participation - quads
    if(quads !== null && quads !== undefined) {
        //TBD
        
        //draw a quad
        
    }      
}

function createRelationsDependencyGeometry() {
    //TBD
}

function doSceneSetup() {
    //get scene geometric extents 
    var boundingBoxes = [];
    
    if(actorPointsGeometry !== undefined && actorPointsGeometry !== null) {
        actorPointsGeometry.computeBoundingSphere();
        actorPointsGeometry.computeBoundingBox(); 
        boundingBoxes.push(actorPointsGeometry.boundingBox);  
    }
    if(eventPointsGeometry !== undefined && eventPointsGeometry !== null) {
        eventPointsGeometry.computeBoundingSphere();
        eventPointsGeometry.computeBoundingBox(); 
        boundingBoxes.push(eventPointsGeometry.boundingBox);  
    }
    
    var rangeMax = new THREE.Vector3();
    var rangeMin = new THREE.Vector3();
    
    rangeMax.x = Number.NEGATIVE_INFINITY;
    rangeMax.y = Number.NEGATIVE_INFINITY;
    rangeMax.z = Number.NEGATIVE_INFINITY;
    rangeMin.x = Number.POSITIVE_INFINITY;
    rangeMin.y = Number.POSITIVE_INFINITY;
    rangeMin.z = Number.POSITIVE_INFINITY;
    
    for (var i = 0; i < boundingBoxes.length; i++) {
        var bbcenter = boundingBoxes[i].getCenter();
        var bbsize = boundingBoxes[i].getSize();
        
        var mx = bbcenter.x + bbsize.x/2;
        if(mx > rangeMax.x)
            rangeMax.x = mx;
        var my = bbcenter.y + bbsize.y/2;
        if(my > rangeMax.y)
            rangeMax.y = my;
        var mz = bbcenter.z + bbsize.z/2;
        if(mz > rangeMax.z)
            rangeMax.z = mz;
        
        var mx = bbcenter.x - bbsize.x/2;
        if(mx < rangeMin.x)
            rangeMin.x = mx;
        var my = bbcenter.y - bbsize.y/2;
        if(my < rangeMin.y)
            rangeMin.y = my;
        var mz = bbcenter.z - bbsize.z/2;
        if(mz < rangeMin.z)
            rangeMin.z = mz;        
        
    } 
    var trueSize = new THREE.Vector3();
    var trueCenter = new THREE.Vector3();
    
    trueSize.x = rangeMax.x - rangeMin.x;
    trueSize.y = rangeMax.y - rangeMin.y;
    trueSize.z = rangeMax.z - rangeMin.z;
    
    trueCenter.x = (rangeMax.x + rangeMin.x)/2;
    trueCenter.y = (rangeMax.y + rangeMin.y)/2;
    trueCenter.z = (rangeMax.z + rangeMin.z)/2;


    //set camera position to view the whole scene
    var maxDim = Math.max(trueSize.x, trueSize.y, trueSize.z);
    var fov = camera.fov * (Math.PI / 180);
    var cameraZ = Math.abs(maxDim / (2 * Math.tan(fov / 2))) * 1.5;
    camera.position.set(trueCenter.x, trueCenter.y, cameraZ);
    controls.update();

    //resize nodes accordingly to scene dimensions
    pointSize = 0.1 * maxDim;
    for (var i = 0; i < nodeSizes.length; i++) {
        nodeSizes[i] = nodeSizes[i] * 20 * pointSize;
    }
    raycaster.linePrecision = pointSize / 8;   
    
    if(actorPointsGeometry !== undefined && actorPointsGeometry !== null)
        actorPointsGeometry.attributes.size.needsUpdate = true;
    if(eventPointsGeometry !== undefined && eventPointsGeometry !== null)
        eventPointsGeometry.attributes.size.needsUpdate = true;
    
    render();
}

function render() {
    //set light position at camera position
    directionalLight.position.set(camera.position.x, camera.position.y, camera.position.z).normalize();
    //do rendering
    renderer.render(scene, camera);
}

function animate() {
    requestAnimationFrame(animate);
    render();
    stats.update();
}

function barcharts() {
    var filteredData = crossfilter(dataobject);
    var all = filteredData.groupAll();
    var fdata = filteredData.dimension(function (d) {
        return  d.segmentFData;
    });
    var fdatas = fdata.group(function (d) {
        return Math.floor(d);
    });


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

function isActorNode(index) {
    if(actorNodeIndices !== null) {
        for (var i = 0; i < actorNodeIndices.length; i++) {
            if(actorNodeIndices[i] == index)
                return true;
        }
    }
    return false;
}

function isEventNode(index) {
    if(eventNodeIndices !== null) {
        for (var i = 0; i < eventNodeIndices.length; i++) {
            if(eventNodeIndices[i] == index)
                return true;
        }
    }
    return false;
}

function processDataObject(inDataObject) {
    dataobject = inDataObject;

    coords = dataobject.coords;
    nNodes = dataobject.nNodes;
    actorNodeIndices = dataobject.actorNodeIndices;
    eventNodeIndices = dataobject.eventNodeIndices;
    nodeDataIDs = dataobject.nodeDataIDs;
    segments = dataobject.segments;
    actorSegmentIndices = dataobject.actorSegmentIndices;
    eventSegmentIndices = dataobject.eventSegmentIndices;
    participSegmentIndices = dataobject.participSegmentIndices;
    segmentDataIDs = dataobject.segmentDataIDs;
    quads = dataobject.quads;
    quadDataIDs = dataobject.quadDataIDs;
    data = dataobject.data;


    nodeSizes = new Float32Array(nNodes);
    for (var i = 0; i < nodeSizes.length; i++) {
        nodeSizes[i] = 0;
    }
    if(actorNodeIndices !== null) {
        for (var i = 0; i < actorNodeIndices.length; i++) {
            nodeSizes[actorNodeIndices[i]] = 1;
        }
    }   
    if(eventNodeIndices !== null) {
        for (var i = 0; i < eventNodeIndices.length; i++) {
            nodeSizes[eventNodeIndices[i]] = 1;
        }
    }     
    
    //create colors of nodes (we'll modify them on selection and hover)
    nodeColors = new Float32Array(nNodes * 3);
    if(actorNodeIndices !== null) {
        for (var i = 0; i < actorNodeIndices.length; i++) {
            cActorDefaultColor.toArray(nodeColors, actorNodeIndices[i]*3);
        }
    }   
    if(eventNodeIndices !== null) {
        for (var i = 0; i < eventNodeIndices.length; i++) {
            cEventDefaultColor.toArray(nodeColors, eventNodeIndices[i]*3);
        }
    } 
    
    nodeSelection = new Uint8Array(nNodes);
    for (var i = 0; i < nNodes; i++) {
        nodeSelection[i] = 0;
    }

    updateCharts();
    draw3D();
    render();
}

function setupColors() {
    cActorDefaultColor = new THREE.Color(0xaaaaaa);
    cActorHoverColor = new THREE.Color(0xff0000);
    cActorSelectedColor = new THREE.Color(0x00ff00);

    cEventDefaultColor = new THREE.Color(0xaaaaff);
    cEventHoverColor = new THREE.Color(0xff0000);
    cEventSelectedColor = new THREE.Color(0x0000ff);
    
    cParticipDefaultColor = new THREE.Color(0xaaaaff);
    cParticipHoverColor = new THREE.Color(0xff0000);
    cParticipSelectedColor = new THREE.Color(0x0000ff);
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
