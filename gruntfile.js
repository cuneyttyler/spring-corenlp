module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-exec');
    grunt.loadNpmTasks('grunt-shell');

    grunt.initConfig({
        // for changes to the front-end code
        watch: {
            files: ['**/*.java'],
            tasks: ['shell']
        },

        concurrent: {
            watch: {
                tasks: ['watch'],
                options: {
                    logConcurrentOutput: true
                }
            }
        },

        shell: {
            command: 'mongod --bind_ip=$IP --dbpath=data --nojournal --rest "$@"',
            options: {
                async: true
            }

        },

        exec: {
            echo_name: {
                cmd: 'echo "Echoes"'
            }
        }

    });

    grunt.registerTask('default', ['watch']);
};