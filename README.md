# Track C: Visualisation of processed data with added dimensions for journals, topics, or reference dependency graphs

The general concept of this track during the code sprint is at least twofold. On one hand, we want to elaborate specific visualization means for bibliographical data (e.g. author networks with additional dimensions for e.g. journals, topics or reference dependency graphs). On the other hand, conceptualization of specific services that fit into the current DARIAH infrastructure landscape is needed and combined with the preconditions provided by the other code sprint tracks (e.g. using data from BibSonomy).


## Track goal
The goal of this track is to create certain blocks or functioanlities of the web-based 3D graph visualization service. Each block or functionality needs to bring the tool closer to its ultimate goal of providing a comprehensive web visualization tool for interactive multidimensional graph data analysis and combination of this too, with external DARIAH services. 


## Agenda
1) General introduction to visualization
2) Graphs and graph visualization
3) VisNow as a generic visualization platform
4) Example: co-authorship graph visualization in VisNow
5) Example: web-based co-authorship graph visualization 
6) Code sprint projects overview
7) Tech-talk: VisNow data model and API
8) Tech-talk: overview of web application template
9) Tech-talk: visualization with WebGL and three.js


## Track projects choice
1) Remodelling of data translation layer (from BibSonomy to JSciC)
2) Graph creation and presentation parametrization
3) Visual representation
4) Integrate BibSonomy querying 
5) Introduce query data filtering
6) Introduce visual data filtering
7) 3D user interaction for additional information
8) 3D user interaction for visual data filtering
9) Introduce 2D visualization for additional data analytics
10) Introduce geospatial data from affiliations (if data available) and visualize collaborations on map
11) Conceptualize and implement usage scenarios


## Prerequisites 
The substantial and technical skills prerequisites depend on the choice of the project and cover one of the following topics:
- Java programming
- Java web programming frameworks (e.g. Spring/SpringBoot)
- General web frontend programming (e.g Angular, JavaScript)
- WebGL/three.js programming
- Vega/Vega-lite/D3.js programming


## Technical preparation
To run all steps of our track and work with codes the following components are required:
- 64-bit Windows, Mac OS or Linux operating system. 
- VisNow v1.3-RC2 - installers can be downloaded [here](https://visnow.icm.edu.pl/index.php/downloads).
- VisNow Plugin DESIR - download [here](https://visnow.icm.edu.pl/index.php/downloads/plugins) and follow the installation instruction.
- Java JDK 8 64-bit - we sugest downloading from Oracle website [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). 
- NetBeans IDE v8.2 for Java EE - download [here](https://netbeans.org/downloads/). Note that all provided projects are in NetBeans project format. If you wish to use another Java IDE you need to convert the projects by yourself. NetBeans for Java EE bundles also GlassFish and Apache Tomcat servers for local running of web apps. 
- [BibSonomy](https://www.bibsonomy.org) account and API key will be required for API querying.
- Project templates for DesirFrontend and DesirBackend from this Git repository. You can pull it directly from NetBeans through Team->Git->Clone and providing URL: https://github.com/DESIR-CodeSprint/TrackC-Visualization.git 

## Links
- [VisNow page](https://visnow.icm.edu.pl/)
- [VisNow project](https://gitlab.com/ICM-VisLab/VisNow)
- [JSciC project](https://gitlab.com/ICM-VisLab/JSciC)
- [JSciC API javadoc](http://javadoc.io/doc/pl.edu.icm/JSciC/1.0)
- [JLargeArrays project](https://gitlab.com/ICM-VisLab/JLargeArrays)
- [JLargeArrays API javadoc](http://javadoc.io/doc/pl.edu.icm/JLargeArrays/1.6)
- [BibSonomy REST API](https://bitbucket.org/bibsonomy/bibsonomy/wiki/documentation/api/REST%20API)


## Contact
- Bartosz Borucki (<b.borucki@icm.edu.pl>)
- Piotr Wendykier (<p.wendykier@icm.edu.pl>)
