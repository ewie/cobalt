module.exports = function (grunt) {

  function configZip(widgetFileName, mainJs) {
    return {
      router: function (fname) {
        if (fname === mainJs) {
          return 'main.js';
        }
        return fname.replace(/^widget\//, '');
      },
      src: [
        'widget/config.xml',
        'widget/index.html',
        'widget/style.css',
        mainJs
      ],
      dest: widgetFileName,
      compression: 'DEFLATE'
    };
  }

  grunt.initConfig({

    browserify: {
      debug: {
        options: {
          bundleOptions: { debug: true }
        },
        files: {
          'widget/main.debug.js': [ 'src/main.js' ]
        }
      },
      release: {
        files: {
          'widget/main.release.js': [ 'src/main.js' ]
        }
      }
    },

    clean: [
      'widget/*.js',
      '*.wgt'
    ],

    jshint: {
      all: [ 'Gruntfile.js', 'src/**/*.js' ],
      options: {
        jshintrc: true
      }
    },

    uglify: {
      options: {
        mangle: true
      },
      release: {
        files: {
          'widget/main.min.js': [ 'widget/main.release.js' ]
        }
      }
    },

    zip: {
      debug: configZip('cobalt-composer-widget.debug.wgt', 'widget/main.debug.js'),
      release: configZip('cobalt-composer-widget.release.wgt', 'widget/main.min.js')
    }

  });

  grunt.loadNpmTasks('grunt-browserify');
  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-zip');

  grunt.registerTask('widget-debug', [
    'jshint',
    'browserify:debug',
    'zip:debug'
  ]);

  grunt.registerTask('widget-release', [
    'jshint',
    'browserify:release',
    'uglify:release',
    'zip:release'
  ]);

};

