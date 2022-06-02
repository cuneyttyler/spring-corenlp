var gulp = require('gulp'),
    watch = require('gulp-watch'),
    exec = require('gulp-exec'),
    run = require('gulp-run');

gulp.task('compile', function() {
    var options = {
        continueOnError: false, // default = false, true means don't emit error event
        pipeStdout: false, // default = false, true means stdout is written to file.contents
        customTemplatingThing: "test" // content passed to gutil.template()
    };
    var reportOptions = {
        err: true, // default = true, false means don't write err
        stderr: true, // default = true, false means don't write stderr
        stdout: true // default = true, false means don't write stdout
    };

    return gulp.src('./**/**')
        .pipe(exec('cd src/main/java', options))
        .pipe(exec('java com.gsu.interpreter.Interpreter', options)); //
});

gulp.task('hello', function() {
    return run('echo Hello World').exec()    // prints "Hello World\n".
        .pipe(gulp.dest('output'))      // writes "Hello World\n" to output/echo.
        ;
});

gulp.task('compile2', function() {
    return gulp.src('./**/**')
        .pipe(run('cd src/main/java;'))
        .pipe(run('java com.gsu.interpreter.Interpreter'));
//        .pipe(gulp.dest('output'));

});

gulp.task('watch', function() {

    gulp.watch('src/main/java/**/*.java', ['compile']);

});