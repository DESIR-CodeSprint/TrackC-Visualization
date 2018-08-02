
/* global THREE */

var app = angular.module('desirApp', []);

var scene;
var camera;
var renderer;
var container3D;
var ambientLight;
var directionalLight;
var pointsObject;
var lineObjects = [];
var raycaster;
var currentIntersectedPoint;
var currentIntersectedLine;
var mouse;
var pointSphere;
var pointSize = 0.1;

var Xscope;
var Xcamera;
var Xrenderer;
var Xscene;
var nWeights;
var egoPoint;
var adjNodes = [];

window.onload = function () {
    if (!Detector.webgl)
        Detector.addGetWebGLMessage();

    container3D = document.getElementById('Container3D');

    var width = 1024;
    var height = 1024;

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xEEEEEE);
    camera = new THREE.PerspectiveCamera(25, width / height, 0.1, 1000);

    renderer = new THREE.WebGLRenderer({antialias: true});
    renderer.setSize(width, height);
    renderer.setPixelRatio(window.devicePixelRatio);

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
    
    container3D.addEventListener('click', onDocumentMouseClick, false);

    container3D.appendChild(renderer.domElement);
    
    // additional information visualization
    
    Xcontainer3D = document.getElementById('AdditionalInfoVis');

    Xscene = new THREE.Scene();
    Xscene.background = new THREE.Color(0xFFFFFF);
    Xcamera = new THREE.PerspectiveCamera(25, width / height, 0.1, 1000);

    Xrenderer = new THREE.WebGLRenderer({antialias: true});
    Xrenderer.setSize(400, 400);
    Xrenderer.setPixelRatio(window.devicePixelRatio);

    // LIGHTS
    Xscene.add(ambientLight);
    Xscene.add(directionalLight);

    Xcontainer3D.appendChild(Xrenderer.domElement);
    
    render();
};

function onDocumentMouseClick(event) {
    event.preventDefault();

    let canvasBounds = renderer.context.canvas.getBoundingClientRect();
    mouse.x = ( ( event.clientX - canvasBounds.left ) / ( canvasBounds.right - canvasBounds.left ) ) * 2 - 1;
    mouse.y = - ( ( event.clientY - canvasBounds.top ) / ( canvasBounds.bottom - canvasBounds.top) ) * 2 + 1;
    
    var addInfo = document.getElementById("add_info");
    
    raycaster.setFromCamera(mouse, camera); 
 
    // find intersections with points
    var intersects = raycaster.intersectObject(pointsObject);
    
    var ok = false;
    var imin;
    var p;
    var pp;

    //nie koniecznie [0], pÄ™tla po wszystkich i min odlegÅ‚oÅ›ci
    if(intersects.length > 0) {
        //distance
        var geometry = pointsObject.geometry;
        var attributes = geometry.attributes;
        var coords = attributes.position.array;
        var minD = 2*pointSize;
        //TBD check only N=?10 first points sorted by distance from camera
        for (var i = 0; i < intersects.length; i++) {
            var ip = intersects[i].point;
            p = new THREE.Vector3(coords[3*intersects[i].index],coords[3*intersects[i].index+1],coords[3*intersects[i].index+2]);
            var d = ip.distanceTo(p);
            if(d < minD) {
                minD = d;
                imin = i;
                pp = p;
            }
        }
        if(minD < pointSize/4)
            ok = true; 
    } 
    if (ok) {
        // find point pps pid in points
        points = pointsObject.geometry.attributes.position.array;
        var pid;
        
        for(i = 0; i < points.length/3; i++) {
            if(points[3*i] == pp.x && points[3*i + 1] == pp.y && points[3*i + 2] == pp.z){
                pid = i;
                break;
            }
        } 
                
        /*var egoGeometry = new THREE.SphereGeometry( pointSize*nWeights[pid], 32, 32 );
        var egoMaterial = new THREE.MeshBasicMaterial( { color: 0x000000 } );
        egoPoint = new THREE.Mesh( egoGeometry, egoMaterial );
        egoPoint.visible = true;
        Xscene.add( egoPoint );*/

        // find adjacent edges
        segments = Xscope.dataobject.segments;
        var segmentsOfIMin = Array();
        for (i = 0; i < segments.length/2; i++) {
            if(segments[2*i] == pid || segments[2*i + 1] == pid){
                segmentsOfIMin.push(i);
            }
        } 
        
        // TODO: check is change of dataobject is submitted
        var eWeights;
        if(Xscope.dataobject.id == "Test1") {
            eWeights = [1,4,3,2];
        }
        else{
            // TODO: define eWeights for all dataobjects (e.g. # of publications)
            eWeights = new Array(segments.length/2).fill(1);
        }
        
        // compute importance of nodes (sum of edge-weights -> # of publications)
        nWeights = Array(points.length/3);        
        for(i = 0; i < points.length/3; i++){
            nWeights[i] = 0;
            for(j = 0; j < segments.length/2; j++){
                if(segments[2*j] == i || segments[2*j + 1] == i) {
                    nWeights[i] += eWeights[j];
                }
            }
        }
        
        // number and description of adjacent edges
        adjacent = "";
        for(x of segmentsOfIMin) {
            adjacent += "<br>" + x + " (" +  Xscope.dataobject.segmentData[x] + ", " + eWeights[x] + ")";
        }

        addInfo.innerHTML = "<br>node: " + pid + " (" + nWeights[pid] + ") <br>name: " + Xscope.dataobject.nodeData[pid] + "<br>coords; " + points[3*intersects[imin].index] + " " + points[3*intersects[imin].index + 1] + " " + points[3*intersects[imin].index +2] + "<br>adjacent edges: " + adjacent;  
            
        // visualize additional information
        if(egoPoint !== undefined)
            Xscene.remove(egoPoint);
        
            for(x in adjNodes){
                Xscene.remove(adjNodes[x]);
            }
        
        adjNodes = [];
        
        // normalize size with max nWeights-value
        maxNWeight = nWeights[0];
        for(x in nWeights){
            if(nWeights[x] > maxNWeight){
                maxNWeight = nWeights[x];
            }
        }
        
        maxEWeight = eWeights[0];
        for(x in eWeights){
            if(eWeights[x] > maxEWeight){
                maxEWeight = eWeights[x];
            }
        }
        
        // add ego node
        var egoGeometry = new THREE.SphereGeometry( pointSize*(nWeights[pid]/maxNWeight)*1.2, 32, 32 );
        var egoMaterial = new THREE.MeshBasicMaterial( { color: 0x000000 } );
        egoPoint = new THREE.Mesh( egoGeometry, egoMaterial );
        egoPoint.visible = true;
        Xscene.add( egoPoint )

        // calculate phi for adjacent nodes -> usage of sphericals
        phi = (360/segmentsOfIMin.length)/360;
        thisphi = 0;
        
        // add adjacent nodes
        for(x in segmentsOfIMin){
            // strong edges are short, weak esges are long 
            radius = (2 - eWeights[segmentsOfIMin[x]]/maxEWeight);
            thisphi += phi;
            adjacentSphericalPosition = new THREE.Spherical(radius, thisphi, 0);
            hlpVec = new THREE.Vector3();
            hlpVec.setFromSpherical(adjacentSphericalPosition);
            adjacentVectorPosition = new THREE.Vector3().set(hlpVec.getComponent(1), hlpVec.getComponent(2), 0);

            curE = segmentsOfIMin[x];
            if(segments[2*curE] == pid) {
                n = segments[2*curE + 1];
            }
            else{
                n = segments[2*curE];
            }

            adjacentNode = new THREE.SphereGeometry(pointSize*(nWeights[n]/maxNWeight)*1.2, 10, 10);
            adjacentMaterial = new THREE.MeshBasicMaterial( { color: 0x888888 } );
            adjacentPoint = new THREE.Mesh( adjacentNode, adjacentMaterial );
            adjacentPoint.position.copy(adjacentVectorPosition);
            adjacentPoint.visible = true;
            adjNodes.push(adjacentPoint);
            Xscene.add( adjacentPoint );
        }
        
    }
    
    render();
}


function onDocumentMouseMove(event) {
    event.preventDefault();

    let canvasBounds = renderer.context.canvas.getBoundingClientRect();
    mouse.x = ( ( event.clientX - canvasBounds.left ) / ( canvasBounds.right - canvasBounds.left ) ) * 2 - 1;
    mouse.y = - ( ( event.clientY - canvasBounds.top ) / ( canvasBounds.bottom - canvasBounds.top) ) * 2 + 1;
    
    var mousePos = document.getElementById("mouse_pos");
    mousePos.innerHTML = mouse.x+", "+mouse.y;
    
    raycaster.setFromCamera(mouse, camera); 
 
    // find intersections with points
    var intersects = raycaster.intersectObject(pointsObject);
    var ok = false;
    var pp;
    var imin;
    
    //nie koniecznie [0], pętla po wszystkich i min odległości
    if(intersects.length > 0) {
        //distance
        var geometry = pointsObject.geometry;
        var attributes = geometry.attributes;
        var coords = attributes.position.array;
        var minD = 2*pointSize;
        //TBD check only N=?10 first points sorted by distance from camera
        for (var i = 0; i < intersects.length; i++) {
            var ip = intersects[i].point;
            var p = new THREE.Vector3(coords[3*intersects[i].index],coords[3*intersects[i].index+1],coords[3*intersects[i].index+2]);
            var d = ip.distanceTo(p);
            if(d < minD) {
                minD = d;
                imin = i;
                pp = p;
            }
        }
        if(minD < pointSize/4)
            ok = true; 
    } 
    
    if (ok) {
        if (currentIntersectedLine !== undefined) {
            currentIntersectedLine.material.linewidth = 1;
        }
        currentIntersectedLine = undefined;
        
        currentIntersectedPoint = intersects[imin].object;
        pointSphere.position.copy(pp);
        pointSphere.visible = true;
    } else {
        currentIntersectedPoint = undefined;
        pointSphere.visible = false;
    }   
    
    // find intersections with lines
    if(!ok) {
        intersects = raycaster.intersectObjects(lineObjects); 
        if (intersects.length > 0) {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
            }
            currentIntersectedLine = intersects[ 0 ].object;
            currentIntersectedLine.material.linewidth = 10;
        } else {
            if (currentIntersectedLine !== undefined) {
                currentIntersectedLine.material.linewidth = 1;
            }
            currentIntersectedLine = undefined;
        } 
    }
    
    render();
}

function draw3D($scope) {   
    if(pointsObject !== undefined)
        scene.remove(pointsObject);
    if(lineObjects !== undefined) {
        for (var i = 0; i < lineObjects.length; i++)
            scene.remove(lineObjects[i]);
        lineObjects = [];
    }
    if(pointSphere !== undefined)
        scene.remove(pointSphere);

    //get object data from scope
    var coords = $scope.dataobject.coords;
    var segments = $scope.dataobject.segments;

    //create nodes geometry
    var pointsGeometry = new THREE.BufferGeometry();
    pointsGeometry.addAttribute( 'position', new THREE.Float32BufferAttribute( coords, 3 ) );
    
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
    
    //create points at nodes
    pointSize = 0.1 * maxDim;
    var pointsMaterial = new THREE.PointsMaterial({color: 0x0000ff, size: pointSize});
    pointsObject = new THREE.Points(pointsGeometry, pointsMaterial);
    scene.add(pointsObject);
    raycaster.linePrecision = pointSize/8;

    //create sphere for point selection
    var sphereGeometry = new THREE.SphereGeometry( pointSize/4, 32, 32 );
	var sphereMaterial = new THREE.MeshBasicMaterial( { color: 0xff0000 } );
    pointSphere = new THREE.Mesh( sphereGeometry, sphereMaterial );
    pointSphere.visible = false;
    scene.add( pointSphere );

    //create segments geometry and lines
    var nSegments = segments.length / 2;
    var a, b;
    for (var i = 0; i < nSegments; i++) {
        var linesGeometry = new THREE.Geometry();
        a = segments[2 * i];
        b = segments[2 * i + 1];
        linesGeometry.vertices.push(new THREE.Vector3(coords[3 * a], coords[3 * a + 1], coords[3 * a + 2]));
        linesGeometry.vertices.push(new THREE.Vector3(coords[3 * b], coords[3 * b + 1], coords[3 * b + 2]));
        var linesMaterial = new THREE.LineBasicMaterial({color: 0xff0000, linewidth: 1});
        var lineObject = new THREE.LineSegments(linesGeometry, linesMaterial);
        lineObjects.push(lineObject);
        scene.add(lineObject);
    }

    // visualize additional information
    Xcamera.position.set(bbcenter.x, bbcenter.y, cameraZ);

    render();
}

function render() {
    //set light position at camera position
    directionalLight.position.set(camera.position.x, camera.position.y, camera.position.z).normalize();
    //do rendering
    renderer.render(scene, camera);
    Xrenderer.render(Xscene, Xcamera);
}

app.controller('RetrieveMyObjectController', ['$scope', '$http', '$q', function ($scope, $http, $q) {
        $scope.retrieveMyObject = function () {
            var ID = $scope.dataobject.id;
            var text = $scope.dataobject.text;

            $http.get('http://localhost:8080/dataobject?id=' + ID + '&text=' + text).
                    then(function (response) {
                        $scope.dataobject = response.data;
                        if ($scope.dataobject.text === "undefined")
                            $scope.dataobject.text = "";

                        var showID = document.getElementById("id_display");
                        showID.innerHTML = $scope.dataobject.id;

                        var showSEQ = document.getElementById("seq_display");
                        showSEQ.innerHTML = ";  Text: " + $scope.dataobject.text;

                        Xscope = $scope;
                        draw3D($scope);
                    });
        };

    }]);







