.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0


APEX Developer Guide
********************

.. contents::
    :depth: 3

Build APEX from Source
^^^^^^^^^^^^^^^^^^^^^^

Introduction to building APEX
-----------------------------

            .. container:: paragraph

               APEX is written 100% in Java and uses `Apache
               Maven <https://maven.apache.org/>`__ as the build system.
               The requirements for building APEX are:

            .. container:: ulist

               -  An installed Java development kit for Java version 8
                  or higher

                  .. container:: ulist

                     -  To install a Java SDK please follow these
                        guidelines `Oracle Java 8
                        SDK <https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html>`__.

               -  Maven 3

                  .. container:: ulist

                     -  To get Maven 3 running please follow the
                        guidelines for
                        `Download <https://maven.apache.org/download.cgi>`__
                        and
                        `Install <https://maven.apache.org/install.html>`__,
                        and `Run <https://maven.apache.org/run.html>`__
                        Maven

               -  A clone of the APEX source repositories

            .. container:: paragraph

               To get a clone of the APEX source repositories, please
               see the APEX Installation Guide or the APEX User manual.

            .. container:: paragraph

               One all requirements are in place, APEX can be build.
               There are several different artifacts one can create
               building APEX, most of them defined in their own
               *profile*. APEX can also be build in a standard way with
               standard tests (``mvn clean install``) or without
               standard tests (``mvn clean install -DskipTests``).

            .. container:: paragraph

               The examples in this document assume that the APEX source
               repositories are cloned to:

            .. container:: ulist

               -  Unix, Cygwin: ``/usr/local/src/apex``

               -  Windows: ``C:\dev\apex``

               -  Cygwin: ``/cygdrive/c/dev/apex``

            .. important:: 
			
			   A Build requires ONAP Nexus
			   APEX has a dependency to ONAP parent projects. You might need to adjust your Maven M2 settings. The most current
			   settings can be found in the ONAP oparent repo: `Settings <https://git.onap.org/oparent/plain/settings.xml>`__.

            .. important:: 
			
			   A Build needs Space
			   Building APEX requires approximately 2-3 GB of hard disc space, 1 GB for the actual build with full
			   distribution and 1-2 GB for the downloaded dependencies

            .. important:: 
			   A Build requires Internet (for first build to download all dependencies and plugins)
			   During the build, several (a lot) of Maven dependencies will be downloaded and stored in the configured local Maven 
			   repository. The first standard build (and any first specific build) requires Internet access to download those
			   dependencies.

            .. important:: 
			   Building RPM distributions
			   RPM images are only build if the ``rpm`` package is installed (Unix). To install ``rpm`` 
			   run ``sudo apt-get install rpm``, then build APEX. 

Standard Build
--------------

            .. container:: paragraph

               Use Maven to for a standard build without any tests.

            +-----------------------------------+------------------------------------+
            | Unix, Cygwin                      | Windows                            |
            +===================================+====================================+
            | ::                                | ::                                 |
            |                                   |                                    |
            |    >c:                            |    # cd /usr/local/src/apex        |
            |    >cd \dev\apex                  |    # mvn clean install -DskipTests |
            |    >mvn clean install -DskipTests |                                    |
            |                                   |                                    |
            +-----------------------------------+------------------------------------+

.. container:: paragraph

   The build takes about 6 minutes on a standard development laptop. It
   should run through without errors, but with a lot of messages from
   the build process.

.. container:: paragraph

   When Maven is finished with the build, the final screen should look
   similar to this (omitting some ``success`` lines):

.. container:: listingblock

   .. code:: bash
     :number-lines:

     [INFO] tools .............................................. SUCCESS [  0.248 s]
     [INFO] tools-common ....................................... SUCCESS [  0.784 s]
     [INFO] simple-wsclient .................................... SUCCESS [  3.303 s]
     [INFO] model-generator .................................... SUCCESS [  0.644 s]
     [INFO] packages ........................................... SUCCESS [  0.336 s]
     [INFO] apex-pdp-package-full .............................. SUCCESS [01:10 min]
     [INFO] Policy APEX PDP - Docker build 2.0.0-SNAPSHOT ...... SUCCESS [ 10.307 s]
     [INFO] ------------------------------------------------------------------------
     [INFO] BUILD SUCCESS
     [INFO] ------------------------------------------------------------------------
     [INFO] Total time: 03:43 min
     [INFO] Finished at: 2018-09-03T11:56:01+01:00
     [INFO] ------------------------------------------------------------------------

.. container:: paragraph

   The build will have created all artifacts required for an APEX
   installation. The following example show how to change to the target
   directory and how it should look like.

+-----------------------------------------------------------------------------------------------------------------------------+
| Unix, Cygwin                                                                                                                |
+=============================================================================================================================+
| .. container::                                                                                                              |
|                                                                                                                             |
|    .. container:: listingblock                                                                                              |
|                                                                                                                             |
|          .. code:: bash                                                                                                     |
|            :number-lines:                                                                                                   |
|                                                                                                                             |
|            # cd packages/apex-pdp-package-full/target                                                                       |
|            # ls -l                                                                                                          |
|            -rwxrwx---+ 1 esvevan Domain Users       772 Sep  3 11:55 apex-pdp-package-full_2.0.0~SNAPSHOT_all.changes*      |
|            -rwxrwx---+ 1 esvevan Domain Users 146328082 Sep  3 11:55 apex-pdp-package-full-2.0.0-SNAPSHOT.deb*              |
|            -rwxrwx---+ 1 esvevan Domain Users     15633 Sep  3 11:54 apex-pdp-package-full-2.0.0-SNAPSHOT.jar*              |
|            -rwxrwx---+ 1 esvevan Domain Users 146296819 Sep  3 11:55 apex-pdp-package-full-2.0.0-SNAPSHOT-tarball.tar.gz*   |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:54 archive-tmp/                                           |
|            -rwxrwx---+ 1 esvevan Domain Users        89 Sep  3 11:54 checkstyle-cachefile*                                  |
|            -rwxrwx---+ 1 esvevan Domain Users     10621 Sep  3 11:54 checkstyle-checker.xml*                                |
|            -rwxrwx---+ 1 esvevan Domain Users       584 Sep  3 11:54 checkstyle-header.txt*                                 |
|            -rwxrwx---+ 1 esvevan Domain Users        86 Sep  3 11:54 checkstyle-result.xml*                                 |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:54 classes/                                               |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:54 dependency-maven-plugin-markers/                       |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:54 etc/                                                   |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:54 examples/                                              |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:55 install_hierarchy/                                     |
|            drwxrwx---+ 1 esvevan Domain Users         0 Sep  3 11:54 maven-archiver/                                        |
+-----------------------------------------------------------------------------------------------------------------------------+

+-----------------------------------------------------------------------------------------------------------------------------+
| Windows                                                                                                                     |
+=============================================================================================================================+
| .. container::                                                                                                              |
|                                                                                                                             |
|    .. container:: listingblock                                                                                              |
|                                                                                                                             |
|          .. code:: bash                                                                                                     |
|            :number-lines:                                                                                                   |
|                                                                                                                             |
|            >cd packages\apex-pdp-package-full\target                                                                        |
|            >dir                                                                                                             |
|                                                                                                                             |
|            03/09/2018  11:55    <DIR>          .                                                                            |
|            03/09/2018  11:55    <DIR>          ..                                                                           |
|            03/09/2018  11:55       146,296,819 apex-pdp-package-full-2.0.0-SNAPSHOT-tarball.tar.gz                          |
|            03/09/2018  11:55       146,328,082 apex-pdp-package-full-2.0.0-SNAPSHOT.deb                                     |
|            03/09/2018  11:54            15,633 apex-pdp-package-full-2.0.0-SNAPSHOT.jar                                     |
|            03/09/2018  11:55               772 apex-pdp-package-full_2.0.0~SNAPSHOT_all.changes                             |
|            03/09/2018  11:54    <DIR>          archive-tmp                                                                  |
|            03/09/2018  11:54                89 checkstyle-cachefile                                                         |
|            03/09/2018  11:54            10,621 checkstyle-checker.xml                                                       |
|            03/09/2018  11:54               584 checkstyle-header.txt                                                        |
|            03/09/2018  11:54                86 checkstyle-result.xml                                                        |
|            03/09/2018  11:54    <DIR>          classes                                                                      |
|            03/09/2018  11:54    <DIR>          dependency-maven-plugin-markers                                              |
|            03/09/2018  11:54    <DIR>          etc                                                                          |
|            03/09/2018  11:54    <DIR>          examples                                                                     |
|            03/09/2018  11:55    <DIR>          install_hierarchy                                                            |
|            03/09/2018  11:54    <DIR>          maven-archiver                                                               |
|                           8 File(s)    292,652,686 bytes                                                                    |
|                           9 Dir(s)  14,138,720,256 bytes free                                                               |
+-----------------------------------------------------------------------------------------------------------------------------+


Checkstyle with Maven
---------------------

   .. container:: paragraph

      The codestyle for all APEX java projects can be checked
      automatically. The checks include empty or non-existing Javadocs.
      Any checkstyle run should complete without any errors, some
      warnings are acceptable.

   .. container:: paragraph

      To run checkstyle on an APEX Maven project use:

   .. container:: listingblock

      .. container:: content

         .. code:: bash

            mvn checkstyle:check

   .. container:: paragraph

      To run checkstyle on all modules use:

   .. container:: listingblock

      .. container:: content

         .. code:: bash 

            mvn checkstyle:checkstyle -DapexAll

Build with standard Tests
-------------------------

   .. container:: paragraph

      Use Maven to for a standard build with standard tests.

   .. important::  
      Some tests have specific timing Requirements
	  Some of the tests have very specific timing requirements. If run on a low-powered build machine, or if the build
	  machine is on high load, those tests might fail and the whole build might fail as well. If this happens, reduce the load
	  on your build machine and restart the build.

   +-----------------------------------+-----------------------------------+
   | Unix, Cygwin                      | Windows                           |
   +===================================+===================================+
   | .. container::                    | .. container::                    |
   |                                   |                                   |
   |    .. container:: content         |    .. container:: content         |
   |                                   |                                   |
   |       .. code:: bash              |       .. code:: bash              |
   |         :number-lines:            |         :number-lines:            |
   |                                   |                                   |
   |         >c:                       |         # cd /usr/local/src/apex  |
   |         >cd \dev\apex             |         # mvn clean install       |
   |         >mvn clean install        |                                   |
   +-----------------------------------+-----------------------------------+


.. container:: paragraph

   The build takes about 10 minutes with tests on a standard development
   laptop. It should run through without errors, but with a lot of
   messages from the build process. If build with tests (i.e. without
   ``-DskipTests``), there will be error messages and stack trace prints
   from some tests. This is normal, as long as the build finishes
   successful.

Build with all Tests
--------------------

   .. container:: paragraph

      Use Maven to for a standard build with *all* tests.

   .. important::  
      Some tests have specific timing Requirements.
	  Some of the tests have very specific timing requirements. If run on a low-powered build machine, or if the build
	  machine is on high load, those tests might fail and the whole build might fail as well. If this happens, reduce the load
	  on your build machine and restart the build.

   .. important::  
      Might require specific software.
	  When running all tests, some modules require specific software installed on the build machine. For instance,
	  testing the full capabilities of context (with distribution and persistence) will require Hazelcast and Infinispan
	  installed on the build machine.

   +----------------------------------------------+----------------------------------------------+
   | Unix, Cygwin                                 | Windows                                      |
   +==============================================+==============================================+
   | .. container::                               | .. container::                               |
   |                                              |                                              |
   |    .. container:: content                    |    .. container:: content                    |
   |                                              |                                              |
   |       .. code:: bash                         |       .. code:: bash                         |
   |         :number-lines:                       |         :number-lines:                       |
   |                                              |                                              |
   |         >c:                                  |         # cd /usr/local/src/apex             |
   |         >cd \dev\apex                        |         # mvn clean install -DallTests       |
   |         >mvn clean install -DallTests        |                                              |
   +----------------------------------------------+----------------------------------------------+

Build with all Components
-------------------------

   .. container:: paragraph

      A standard APEX build will not build all components. Some parts
      are for specific deployments, only. Use Maven to for a standard
      build with *all* components.

   .. important:: 
      Might require specific software.
	  When building all components, some modules require specific software installed on the build machine.

   +----------------------------------------------+----------------------------------------------+
   | Unix, Cygwin                                 | Windows                                      |
   +==============================================+==============================================+
   | .. container::                               | .. container::                               |
   |                                              |                                              |
   |    .. container:: content                    |    .. container:: content                    |
   |                                              |                                              |
   |       .. code:: bash                         |       .. code:: bash                         |
   |         :number-lines:                       |         :number-lines:                       |
   |                                              |                                              |
   |         >c:                                  |         # cd /usr/local/src/apex             |
   |         >cd \dev\apex                        |         # mvn clean install -DapexAll        |
   |         >mvn clean install -DapexAll         |                                              |
   +----------------------------------------------+----------------------------------------------+


Build the APEX Documentation
----------------------------

   .. container:: paragraph

      The APEX Maven build also includes stand-alone documentations,
      such as the HowTo documents, the Installation Guide, and the User
      Manual. Use Maven to build the APEX Documentation. The Maven
      options ``-N`` prevents Maven to go through all APEX modules,
      which is not necessary for the documentation. The final documents
      will be in ``target/generated-docs`` (Windows:
      ``target\generated-docs``). The *HTML* documents are in the
      ``html/`` folder, the *PDF* documents are in the ``pdf/`` folder.
      Once the documentation is build, copy the *HTML* and *PDF*
      documents to a folder of choice

   +-------------------------------------------------------+--------------------------------------------------------+
   | Unix, Cygwin                                          | Windows                                                |
   +=======================================================+========================================================+
   | .. container::                                        | .. container::                                         |
   |                                                       |                                                        |
   |    .. container:: content                             |    .. container:: content                              |
   |                                                       |                                                        |
   |       .. code:: bash                                  |       .. code:: bash                                   |
   |         :number-lines:                                |         :number-lines:                                 |
   |                                                       |                                                        |
   |         >c:                                           |         # cd /usr/local/src/apex                       |
   |         >cd \dev\apex                                 |         # mvn clean generate-resources -N -DapexDocs   |
   |         >mvn clean generate-resources -N -DapexDocs   |                                                        |
   +-------------------------------------------------------+--------------------------------------------------------+

Build APEX Site
---------------

   .. container:: paragraph

      The APEX Maven build comes with full support to build a web site
      using Maven Site. Use Maven to build the APEX Site. Stage the APEX
      web site. The target folder for the staged site is

   .. container:: ulist

      -  Unix: ``/usr/local/src/apex/target/ad-site``

      -  Windows: ``C:\dev\apex\target\ad-site``

      -  Cygwin: ``/cygdrive/c/dev/apex/target/ad-site``

   .. container:: paragraph

      Once the web site is staged, copy the full site to a folder of
      choice or into a web server.

   .. important::  
      Building a Site takes Time.
	  Building and staging the APEX web site can take very long. The stand-alone documentation will take about 2 minutes. The
	  sites for all modules and projects and the main APEX site can take between 10-30 minutes depending on your build machine (~10 minutes
	  without generating source and test-source reports, closer to 30 minutes with all reports).

   .. container:: paragraph

      Start the build deleting the staging directory that might have
      been created by a previous site build. Then go to the APEX
      packaging directory.

   +--------------------------------+-----------------------------------+----------------------------------+
   | Unix                           | Windows                           | Cygwin                           |
   +================================+===================================+==================================+
   | .. container::                 | .. container::                    | .. container::                   |
   |                                |                                   |                                  |
   |    .. container:: content      |    .. container:: content         |    .. container:: content        |
   |                                |                                   |                                  |
   |       .. code:: bash           |       .. code:: bash              |       .. code:: bash             |
   |         :number-lines:         |         :number-lines:            |         :number-lines:           |
   |                                |                                   |                                  |
   |         cd /usr/local/src/apex |         c:                        |         cd /cygdrive/c/dev/apex  |
   |         rm -fr target/ad-site  |         cd \dev\apex              |         rm -fr target/ad-site    |
   |                                |         rmdir /s/q target\ad-site |                                  |
   +--------------------------------+-----------------------------------+----------------------------------+

   .. container:: paragraph

      the workflow for building a complete site then is as follows:

   .. container:: listingblock

      .. container:: content

         .. code:: bash

            mvn clean -DapexAll (1)
            mvn install -DskipTests (2)
            mvn generate-resources -N -DapexDocs (3)
            mvn initialize site:attach-descriptor site site:stage -DapexSite (4)

   .. container:: olist arabic

      #. First clean all modules to remove any site artifacts, use the
         *apexXtext* profile to make sure these modules are processed as
         well

      #. Next run a simple install without tests

      #. Now generate the APEX stand -alone documentation, they are in
         the local package only so we can use the *-N* switch

      #. Last build the actual sites and stage (copy to the staging
         directory) with the profile *apexSite* (do not forget the
         initialize goal, otherwise the staging directory will not be
         correctly set and sites are staged in every model in a
         directory called ``docs``).

   .. container:: paragraph

      If you want to build the site for a particular project for
      testing, the Maven command is simpler. Since only the main project
      has APEX documentation (stand-alone), you can use Maven as follow.

   .. container:: listingblock

      .. container:: content

         .. code:: bash

            mvn clean site -DapexSite

   .. container:: paragraph

      If you want to stage the tested site, then use

   .. container:: listingblock

      .. container:: content

         .. code:: bash

            mvn clean initialize site:attach-descriptor site site:stage -DapexSite

APEX Codestyle
^^^^^^^^^^^^^^

Introduction: APEX Codestyle
----------------------------

         .. container:: paragraph

            This page describes how to apply a code style to the APEX
            Java projects. The provided code templates are guidelines
            and are provided for references and as examples. We will not
            engage in "holy war" on style for coding. As long as the
            style of a particular block of code is understandable,
            consistent, and readable, please feel free to adapt or
            modify these guides or use other guides as you see fit.

         .. container:: paragraph

            The JAutoDoc and Checkstyle Eclipse Plugins and tools are
            useful and remove a lot of the tedium from code
            documentation. Use them to check your code and please fix
            any issues they identify with your code.

         .. container:: paragraph

            Since APEX is part of ONAP, the general ONAP rules and
            guideliness for development do apply. Please see `ONAP
            Wiki <https://wiki.onap.org/display/DW/Developing+ONAP>`__
            for details.

Java coding Rules
-----------------

         .. container:: ulist

            -  APEX is (in large parts) a platform (or middleware), so
               `Software Design
               Patterns <https://en.wikipedia.org/wiki/Software_design_pattern>`__
               are a good thing

            -  The `Solid
               Principles <https://en.wikipedia.org/wiki/SOLID_(object-oriented_design)>`__
               apply

            -  Avoid class fields scoped as ``protected``

               .. container:: ulist

                  -  They break a lot of good design rules, e.g. most
                     SOLID rules

                  -  For a discussion see this `Stackoverflow
                     Question <https://softwareengineering.stackexchange.com/questions/162643/why-is-clean-code-suggesting-avoiding-protected-variables>`__

            -  If you absolutely need ``protected`` class fields they
               should be ``final``

            -  Avoid ``default`` scope for class fields and methods

               .. container:: ulist

                  -  For fields: use ``public`` or ``private`` (see also
                     above)

                  -  For methods: use ``public`` for general use,
                     ``protected`` for specialization using inheritance
                     (ideally ``final``), ``private`` for everything
                     else

            -  Method parameters that are not changed in the method
               should be marked ``final``

            -  Every package must have a ``package-info.java`` file with
               an appropriate description, minimum a descriptive one
               liner

            -  Every class must have

               .. container:: ulist

                  -  The common header (copyright, file, date)

                  -  Javadoc header for the class with description of
                     the class and author

                  -  Javadoc for *all public\_* fields

                  -  If possible, Javadoc for *private* fields, at least
                     some documentation for private fields

                  -  Javadoc for *all* methods

            -  All project must build with all tests on Unix, Windows,
               *and* Cygwin

               .. container:: ulist

                  -  Support all line endings in files, e.g. ``\n`` and
                     ``\r\n``

                  -  Be aware of potential differences in exception
                     messages, if testing against a message

                  -  Support all types of paths: Unix with ``/``,
                     Windows with an optinal drive ``C:\`` and ``\``,
                     Cygwin with mixed paths

Eclipse Plugin: JAutodoc
------------------------

         .. container:: paragraph

            This plugin is a helper plugin for writing Javadoc. It will
            automatically create standard headers on files, create
            package-info.java files and will put in remarkably good stub
            Javadoc comments in your code, using class names and method
            names as hints.

         .. container:: paragraph

            Available from the Eclipse Marketplace. In Eclipse
            Help→Eclipse Marketplace…​ and type ``JAutodoc``. Select
            JAutodoc when the search returns and install it.

         .. container:: paragraph

            You must configure JAutoDoc in order to get the most out of
            it. Ideally JAutoDoc should be configured with templates
            that cooperate with the inbuilt Eclipse Code Formatter for
            best results.

Eclipse Plugin: Checkstyle
--------------------------

         .. container:: paragraph

            This plugin integrates
            `Checkstyle <http://checkstyle.sourceforge.net/>`__ into
            Eclipse. It will check your code and flag any checkstyle
            issues as warnings in the code.

         .. container:: paragraph

            Available from the Eclipse Marketplace. In Eclipse
            Help→Eclipse Marketplace…​ and type "Checkstyle". Select
            "Checkstyle Plug-in" when the search returns and install it.
            Note that "Checkstyle Plug-in" may not be the first result
            in the list of items returned.

         .. container:: paragraph

            For APEX, the ONAP checkstyle rules do apply. The
            configuration is part of the ONAP parent. See `ONAP
            Git <https://git.onap.org/oparent/plain/checkstyle/src/main/resources/onap-checkstyle/>`__
            for details and updates. All settings for checkstyle are
            already part of the code (POM files).

Configure Eclipse
-----------------

         .. container:: ulist

            -  Set the template for Eclipse code clean up

               .. container:: olist arabic

                  #. Eclipse  Window  Preferences  Java  Code Style 
                     Clean Up → Import…​

                  #. Select your template file
                     (``ApexCleanUpTemplate.xml``) and apply it

            -  Set the Eclipse code templates

               .. container:: olist arabic

                  #. Eclipse  Window  Preferences  Java  Code Style 
                     Code Templates → Import…​

                  #. Select your templates file
                     (``ApexCodeTemplates.xml``) and apply it

                     .. container:: ulist

                        -  Make sure to set your email address in
                           generated comments by selecting
                           "Comments→Types" in the "Configure generated
                           code and comments:" pane, then change the
                           email address on the @author tag to be your
                           email address

            -  Set the Eclipse Formatter profile

               .. container:: olist arabic

                  #. Eclipse  Window  Preferences  Java  Code Style 
                     Formatter → Import…​

                  #. Select your formatter profile file
                     (``ApexFormatterProfile.xml``) and apply it

         .. container:: paragraph

            The templates mentioned above can be found in
            ``apex-model/apex-model.build-tools/src/main/resources/eclipse``

Configure JAutodoc (Eclipse)
----------------------------

         .. container:: paragraph

            Import the settings for JAutodoc:

         .. container:: olist arabic

            #. Eclipse  Window  Preferences  Java  JAutodoc → Import
               All…​ (at bottom of the JAutodoc preferences window)

            #. Leave all the preferences ticked to import all
               preferences, browse to the JAutodoc setting file
               (``ApexJautodocSettings.xml``) and press OK

            #. Set your email address in the package Javadoc template

               .. container:: ulist

                  -  Press Edit Template…​ in the Package Javadoc area
                     of the JAutodoc preferences window, and change the
                     email address on the ``@author`` tag to be your
                     email address

            #. Now, apply the JAutodoc settings

         .. container:: paragraph

            The templates mentioned above can be found in
            ``apex-model/apex-model.build-tools/src/main/resources/eclipse``

Configure Checkstyle (Maven)
----------------------------

         .. container:: paragraph

            When using a custom style configuration with Checkstyle, the
            definition of that style must of course be available to
            Checkstyle. In order not to have to distribute style files
            for checkstyle into all Maven modules, it is recommended
            that a special Maven module be built that contains the
            checkstyle style definition. That module is then used as a
            dependency in the *POM* for all other modules that wish to
            use that checkstyle style. For a full explanation see `the
            explanation of Checkstyle multi-module
            configuration <https://maven.apache.org/plugins/maven-checkstyle-plugin/examples/multi-module-config.html>`__.

         .. container:: paragraph

            For APEX, the ONAP checkstyle rules do apply. The
            configuration is part of the ONAP parent. See `ONAP
            Git <https://git.onap.org/oparent/plain/checkstyle/src/main/resources/onap-checkstyle/>`__
            for details and updates.

Run Checkstyle (Maven)
----------------------

         .. container:: paragraph

            Run Checkstyle using Maven on the command line with the
            command:

         .. container:: listingblock

            .. container:: content

               .. code:: bash

                  mvn checkstyle:check

         .. container:: paragraph

            On the main APEX project, run a full checkstyle check as:

         .. container:: listingblock

            .. container:: content

               .. code:: bash

                  mvn checkstyle:checkstyle -DapexAll

Configure Checkstyle (Eclipse, globally)
----------------------------------------

         .. container:: olist arabic

            #. Set up a module with the Checkstyle style files (see
               above)

            #. In Eclipse  Window  Preferences go to Checkstyle

            #. Import the settings for Checkstyle

               .. container:: ulist

                  -  Press New…​ to create a new *Global Check
                     Configurations* entry

                  -  Give the configuration a name such as *Apex
                     Checkstyle Configuration* and select the *External
                     Configuration File* form in the *Type* drop down
                     menu

                  -  Browse to the Checckstyle setting file
                     (``ApexCheckstyleSettings.xml``) and press OK

            #. Press OK

               .. container:: ulist

                  -  You may now get an *Unresolved Properties found*
                     dialogue

                  -  This is because there is a second Checkstyle
                     configuration file required to check file headers

            #. Press Edit Properties…​ and press Find unresolved
               properties on the next dialogue window

            #. The plugin will find the ``${checkstyle.header.file}``
               property is unresolved and will ask should it be added to
               the properties, click yes

            #. Now, select the row on the dialogue for the
               ``checkstyle.header.file property`` and click Edit…​

            #. Set the value of the ``checkstyle.header.file property``
               to
               ``<your-apex-git-location>/apex-model/apex-model.build-tools/src/main/resources/checkstyle/apex_header.txt``

               .. container:: ulist

                  -  Of course replacing the tag
                     ``<your-apex-git-location>`` with the location of
                     your Apex GIT repository

            #. Press OK, OK, OK to back out to the main Checkstyle
               properties window

            #. Select the *Apex Checkstyle Configuration* as your
               default configuration by selecting its line in the
               *Global Check Configuraitons* list and clicking Set as
               Default

            #. Press Apply and Close to finish Checkstyle global
               configuration

         .. container:: paragraph

            The templates mentioned above can be found in
            ``apex-model/apex-model.build-tools/src/main/resources/eclipse``

2.10. Configure Checkstyle Blueprint
------------------------------------

         .. container:: paragraph

            As well as being configured globally, Checkstyle must be
            configured and activated for each project in Eclipse. In
            order to make this process less tedious, set up the first
            project you apply Checkstye to as a blueprint project and
            then use this blueprint for all other projects.

         .. container:: olist arabic

            #. Select the project you want to use as a blueprint

               .. container:: ulist

                  -  For example, ``apex-model.basic-model`` in ``apex``
                     and enter the project properties by right clicking
                     and selecting **Properties**

            #. Click *Checkstyle* on the properties to get the
               Checkstyle project configuration window

            #. Click the box *Checkstyle active for this project* and in
               the *Exclude from checking…​* list check the boxes:

               .. container:: ulist checklist

                  -   *files outside source directories*

                  -   *derived (generated) files*

                  -   *files from packages:*

            #. Now, in order to turn off checking on resource
               directories and on JUnit tests

               .. container:: ulist

                  -  Select the line *files from packages:* in the
                     *Exclude from checking…​* list and click Change…​

            #. On the *Filter packages* dialogue

               .. container:: ulist

                  -  Check all the boxes except the top box, which is
                     the box for *src/main/java*

                  -  Ensure that the *recursively exclude sub-packages*
                     check box is ticked

                     .. container:: ulist checklist

                        -   *recursively exclude sub-packages*

                  -  Press OK

            #. Press Apply and Close to apply the changes

Use Eclipse Source Operations
-----------------------------

         .. container:: paragraph

            Eclipse Source Operations can be carried out on individual
            files or on all the files in a package but do not recurse
            into sub-packages. They are available as a menu in Eclipse
            by selecting a file or package and right clicking on
            *Source*. Note that running *Clean Up…​* with the Apex clean
            up profile will run *Format* and *Organize Imports*. So if
            you run a clean up on a file or package, you need not run
            *Format* or *Organize Imports*.

         .. container:: paragraph

            We recommend you use the following Eclipse Source
            Operations:

         .. container:: olist arabic

            #. *Format* applies the current format definition to the
               file or all files in a package

            #. *Organize Imports* sorts the imports on each file in
               standard order

            #. *Clean Up* runs a number of cleaning operations on each
               file. The Apex clean up template

               .. container:: ulist

                  -  Remove ``this`` qualifier for non static field
                     accesses

                  -  Change non static accesses to static members using
                     declaring type

                  -  Change indirect accesses to static members to
                     direct accesses (accesses through subtypes)

                  -  Convert control statement bodies to block

                  -  Convert ``for`` loops to enhanced ``for`` loops

                  -  Add final modifier to private fields

                  -  Add final modifier to local variables

                  -  Remove unused imports

                  -  Remove unused private methods

                  -  Remove unused private constructors

                  -  Remove unused private types

                  -  Remove unused private fields

                  -  Remove unused local variables

                  -  Add missing ``@Override`` annotations

                  -  Add missing ``@Override`` annotations to
                     implementations of interface methods

                  -  Add missing ``@Deprecated`` annotations

                  -  Add missing serial version ID (generated)

                  -  Remove unnecessary casts

                  -  Remove unnecessary ``$NON-NLS$`` tags

                  -  Organize imports

                  -  Format source code

                  -  Remove trailing white spaces on all lines

                  -  Correct indentation

                  -  Remove redundant type arguments

                  -  Add file header (JAutodoc)

Using JAutodoc
--------------

         .. container:: paragraph

            Similar to Eclipse Source Operations, JAutodoc operations
            can be carried out on individual files or on all the files
            in a package but do not recurse into sub-packages. The
            JAutodoc operations are available by selecting a file or
            package and right clicking on *JAutodoc*:

         .. container:: olist arabic

            #. To add a ``package-info.java`` file to a package, select
               the package and right-click Jautodoc  Add Package Javadoc

            #. To add headers to files select on a file (or on the
               package to do all files) and right click JAutodoc  Add
               Header

            #. To add JAutodoc stubs to a files, select on a file (or on
               the package to do all files) and right click JAutodoc 
               Add Javadoc

Using Checkstyle
----------------

         .. container:: paragraph

            In order to use Checkstyle, you must configure it per
            project and then activate it per project. The easiest way to
            do this is to set up one project as a blueprint and use that
            blueprint for other projects (see above). Once you have a
            blueprint project, you can use Checkstyle on other projects
            as follows

         .. container:: olist arabic

            #. Set up Checkstyle on projects by selecting one or more
               projects

               .. container:: ulist

                  -  Right clicking and selecting Checkstyle  Configure
                     project(s) from *blueprint…​* and then selecting
                     your blueprint project

                  -  (for example ``apex-model.basic-model``) from the
                     list of projects and pressing OK

            #. Activate Checkstyle on projects by selecting one or more
               projects

               .. container:: ulist

                  -  Right clicking and selecting Checkstyle  Activate
                     Checkstyle

                  -  Now Checkstyle warnings will appear on the selected
                     projects if they have warnings

            #. You can disable Checkstyle checking on a file or a
               package (recursively) by selecting a file or package

               .. container:: ulist

                  -  Right clicking and selecting Checkstyle  Clear
                     Checkstyle violations

            #. You can enable Checkstyle checking on a file or a package
               (recursively) by selecting a file or package

               .. container:: ulist

                  -  Right clicking and selecting Checkstyle  Check Code
                     with Checkstyle

            #. On individual files, you can apply fixes that clear some
               Checkstyle warnings

               .. container:: ulist

                  -  Select the file, right click and select **Apply
                     Checkstyle fixes**

Disable Eclipse Formatting (partially)
--------------------------------------

         .. container:: paragraph

            Sometimes, the Eclipse code formatting results in correct
            but untidy indentation, for example when Java Persistence
            annotations or long sequences of lined-up assignments are
            formatted. You can disable formatting for sections of code.

         .. container:: olist arabic

            #. Ensure that Off/On Tags are enabled in Eclipse

            #. In Eclipse  Window  Preferences  Java  Code Style 
               Formatter window press Edit…​

            #. Click on the *Off/On Tags* tab

            #. Ensure that the *Enable Off/On Tags* checkbox is checked

            #. Surround the section of code that you do not want the
               formatter to act on with comments containing the Off/On
               tags

         .. container:: listingblock

            .. container:: content

               .. code:: java
                 :number-lines:

                 // @formatter:off
                 // Plugin Parameters
                 private DistributorParameters distributorParameters = new DistributorParameters();
                 private SchemaParameters      schemaParameters      = new SchemaParameters();
                 private LockManagerParameters lockManagerParameters = new LockManagerParameters();
                 private PersistorParameters   persistorParameters   = new PersistorParameters();
                 // @formatter:on

Supress Checkstyle (partially)
------------------------------

   .. container:: paragraph

      Sometimes Checkstyle checks identify code that does not comply
      with Checkstyle rules. In limited cases Checkstyle rules can be
      suppressed, for example where it is impossible to design the code
      in a way that complies with Checkstyle or where the Checkstyle
      rule is impossible to apply. Checkstyle rules are suppressed as is
      explained in this `Stackoverflow
      post <https://stackoverflow.com/questions/4023185/how-to-disable-a-particular-checkstyle-rule-for-a-particular-line-of-code>`__.

   .. container:: paragraph

      The example below illustrates how to suppress a Checkstyle rule
      that specifies all methods must have seven parameters or less.

   .. container:: listingblock

      .. container:: content

         .. code:: java
            :number-lines:

            // CHECKSTYLE:OFF: checkstyle:ParameterNumber
            public myMethod(final int par1, final int par2, final int par3, final int par4,
              final int par5, final int par6, final int par7, final int par8) {
            }
            // CHECKSTYLE:ON: checkstyle:ParameterNumber

apex-apps.utilities
^^^^^^^^^^^^^^^^^^^

CLI Example
-----------

         .. container:: paragraph

            Using the APEX CLI utilities can be done as follows. First,
            add the dependency of the utility project to your POM file.

         .. container:: listingblock

            .. container:: content

               .. code:: bash

                  <dependency>
                    <groupId>org.onap.policy.apex-pdp.tools</groupId>
                    <artifactId>tools-common</artifactId>
                    <version>2.0.0-SNAPSHOT</version>
                  </dependency>

         .. container:: paragraph

            Now, create a new application project, for instance
            ``MyApp``. In this project, create a new main application
            class as ``Application.java``. In this class, create a new
            main method as ``public static void main(String[] args)``.

         .. container:: paragraph

            No use the provided ``CliOptions`` and ``CliParser``.
            Manually importing means to add the following lines to the
            start of your application (in Eclipse this import will be
            done automatically):

         .. container:: listingblock

            .. container:: content

               .. code:: java
                  :number-lines:

                  import org.onap.policy.apex.tools.common.CliOptions;
                  import org.onap.policy.apex.tools.common.CliParser;

.. container:: paragraph

   Now, inside your ``main()`` method, start setting some general
   application properties. Important are the application name and some
   description of your application. For instance:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         String appName = "test-app";
         final String appDescription = "a test app for documenting how to use the CLI utilities";

.. container:: paragraph

   Next, create a new CLI Parser and add a few CLI options from the
   standard ``CliOptions``. The following example adds options for help,
   version, and a model file:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         final CliParser cli = new CliParser();
         cli.addOption(CliOptions.HELP);
         cli.addOption(CliOptions.VERSION);
         cli.addOption(CliOptions.MODELFILE);

.. container:: paragraph

   Next, parse the given CLI arguments:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         final CommandLine cmd = cli.parseCli(args);

.. container:: paragraph

   Once the command line is parsed, we can look into the individual
   options, check if they are set, and then act accordingly. We start
   with the option for *help*. If the option is present, we print a help
   screen and return:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         // help is an exit option, print usage and exit
         if (cmd.hasOption('h') || cmd.hasOption("help")) {
             final HelpFormatter formatter = new HelpFormatter();
             LOGGER.info(appName + " v" + cli.getAppVersion() + " - " + appDescription);
             formatter.printHelp(appName, cli.getOptions());
             return;
         }

.. container:: paragraph

   Next, we process the option for *version*. Here, we want to print a
   version for our application and return. The CLI Parser already
   provides a method to obtain the correct version for an APEX build, so
   we use that:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         // version is an exit option, print version and exit
         if (cmd.hasOption('v') || cmd.hasOption("version")) {
             LOGGER.info(appName + " " + cli.getAppVersion());
             return;
         }

.. container:: paragraph

   Once help and version arguments are processed, we can proceed to look
   at all other options. We have added an option for a model file, so
   check this option and test if we can actually load a model file with
   the given argument. If we can load a model, everything is ok. If we
   cannot load a model, we print an error and return.

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         String modelFile = cmd.getOptionValue('m');
         if (modelFile == null) {
             modelFile = cmd.getOptionValue("model");
         }
         if (modelFile == null) {
             LOGGER.error(appName + ": no model file given, cannot proceed (try -h for help)");
             return;
         }

.. container:: paragraph

   With a model file being loadable, we finish parsing command line
   arguments. We also print some status messages to note that the
   application now is ready to start:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         LOGGER.info(appName + ": starting");
         LOGGER.info(" --> model file: " + modelFile);

.. container:: paragraph

   The last action now is to run the actual application. The example
   below is taken from a version of the ``Model2Cli`` application, which
   creates a new object and runs it in a ``try`` block, since exceptions
   might be thrown by the object:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         // your code for the application here
         // e.g.
         // try {
         // Model2Cli app = new Model2Cli(modelFile, !cmd.hasOption("sv"), appName);
         // app.runApp();
         // }
         // catch(ApexException aex) {
         // LOGGER.error(appName + ": caught APEX exception with message: " + aex.getMessage());
         // }

.. container:: paragraph

   If this new application is now called with the command line ``-h`` or
   ``--help`` it will print the following help screen:

.. container:: listingblock

   .. container:: content

      .. code:: bash

         test-app v2.0.0-SNAPSHOT - a test app for documenting how to use the CLI utilities
         usage: test-app
          -h,--help                 prints this help and usage screen
          -m,--model <MODEL-FILE>   set the input policy model file
          -v,--version              prints the application version

.. container:: paragraph

   If this new application is called with the option ``-v`` or
   ``--version`` it will print its version information as:

.. container:: listingblock

   .. container:: content

      .. code:: bash

         test-app 2.0.0-SNAPSHOT

Autoversioning an Application
-----------------------------

   .. container:: paragraph

      The APEX utilities project provides means to versioning an
      application automatically towards the APEX version it is written
      for. This is realized by generating a file called
      ``app-version.txt`` that includes the Maven project version. This
      file is then automatically deployed in the folder ``etc`` of a
      full APEX distribution. The CLI Parser here provides a mthod to
      access this version for an application.

   .. container:: paragraph

      First, create a new CLI Parser object, add some options (in the
      example an option for version, but any options will do), then
      parse the command line:

   .. container:: listingblock

      .. container:: content

         .. code:: java
            :number-lines:

            final CliParser cli = new CliParser();
            cli.addOption(CliOptions.VERSION);
            final CommandLine cmd = cli.parseCli(args);

.. container:: paragraph

   Next, we check if the version option was used in the command line and
   print application name and version if it was used:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         // version is an exit option, print version and exit
         if (cmd.hasOption('v') || cmd.hasOption("version")) {
             LOGGER.info("myApp" + " " + cli.getAppVersion());
             return;
         }

.. container:: paragraph

   The output will be:

.. container:: listingblock

   .. container:: content

      .. code:: bash

         myApp 2.0.0-SNAPSHOT

.. container:: paragraph

   The auto-version information comes from the method call
   ``cli.getAppVersion()`` in line 2 in the example above. The method is
   defined in the ``CliParser`` class as:

.. container:: listingblock

   .. container:: content

      .. code:: java
         :number-lines:

         public String getAppVersion() {
             return new Scanner(CliParser.class.getResourceAsStream("/app-version.txt"), "UTF-8").useDelimiter("\\A").next();
         }

.. container:: paragraph

   The file ``app-version.txt`` is automatically added to an APEX full
   distribution, as described above (for details on this see the POM
   files in the APEX application packaging projects).

.. container::
   :name: footer

   .. container::
      :name: footer-text

      2.0.0-SNAPSHOT
      Last updated 2018-09-04 16:04:24 IST
