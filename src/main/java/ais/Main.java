package ais;

import ais.service.StudentService;
import ais.repository.StudentRepository;
import ais.view.StudentView;
import ais.controller.StudentController;

import ais.service.TeacherService;
import ais.repository.TeacherRepository;
import ais.view.TeacherView;
import ais.controller.TeacherController;

import ais.service.SubjectService;
import ais.repository.SubjectRepository;
import ais.view.SubjectView;
import ais.controller.SubjectController;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage stage) {
    StudentService studentservice = new StudentService(new StudentRepository());
    StudentView studentview = new StudentView();
    new StudentController(studentservice, studentview);

    // Scene scene = new Scene(view.getView(), 400, 450);
    // stage.setTitle("Student System");
    // stage.setScene(scene);
    // stage.show();

    TeacherService teacherService = new TeacherService(new TeacherRepository());
    TeacherView teacherView = new TeacherView();
    new TeacherController(teacherService, teacherView);

    // Scene teacherScene = new Scene(teacherView.getView(), 400, 450);
    // stage.setTitle("Teacher System");
    // stage.setScene(teacherScene);
    // stage.show();
    //
    SubjectService subjectservice = new SubjectService(new SubjectRepository());
    SubjectView subjectview = new SubjectView();
    new SubjectController(subjectservice, subjectview);

    Scene scene = new Scene(subjectview.getView(), 400, 450);
    stage.setTitle("Subject System");
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
