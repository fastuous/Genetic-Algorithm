directories:
  src/
    Contains all images (in src/images) and source files (in src/trianglegenome Java and JavaFX FXML)
  api-doc/
    Contains all of the documentation for trianglegenome API
  doc/
    Contains all user documentation.
  kernels/
    Contains OpenCL kernels (currently the only kernel present is used for fitness evaluation).
  src/gui
    Contains all the source code for drawing the code and updating the images
  src/testing
    Contains unit testing and assert tests
  src/util
    Contains utility functions for constants and XML parsing
  src/trianglegenome
    Contains the bulk of the code for the programming. The Model and Presenter parts of our code
usage:
  Once our JAR file has been executed, the GUI will load and the user will have several options:
    select an image:
      Chooses one of four preset images to evolve. Can only be selected if the evolver is not running.
    Tribe selector: 
      Picks one of the threads to display the genomes that lie within. Only selectable if the evolver is not running.
    Genome Slider:
      Can be used to select a particular genome within a tribe. Can only be selected if the evolver is not running.
    Triangle Slider:
      Can be used to view a selected number of the 200 triangles in a genome. Only selectable if the evolver is not running.
    Start Button:
      Toggles running state. When started, it starts the image evolver and disables the other buttons.
    Next Generation:
      Performs one evolution on every genome. Only selectable if the evolver is not running.
    Show Genome Table:
      Displays a genome table that has information on every genome in a tribe. Only selectable if the evolver is not running.
    Read Genome:
      Brings up the last written genome. Only selectable if the evolver is not running.
    Write Genome:
      Writes a genome to an XML file. Only selectable if the evolver is not running.
      
   
    