package ais.view;

import ais.model.Course;
import ais.model.Teacher;
import ais.service.CourseService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class CourseListView extends VBox {

  private final CourseService courseService;
  private final Teacher currentTeacher;
  private ListView<Course> courseListView;
  private TextArea detailsArea;

  public CourseListView(CourseService courseService, Teacher currentTeacher) {
    this.courseService = courseService;
    this.currentTeacher = currentTeacher;
    initUI();
    loadCourses();
  }

  private void initUI() {
    setPadding(new Insets(20));
    setSpacing(15);

    Label titleLabel = new Label("My Courses (Read-Only)");
    titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    Label infoLabel = new Label("Showing only courses with subjects you teach. You cannot edit courses.");
    infoLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");

    Label listLabel = new Label("Your Courses:");
    listLabel.setStyle("-fx-font-weight: bold;");

    courseListView = new ListView<>();
    courseListView.setPrefHeight(300);

    courseListView.setOnMouseClicked(e -> {
      Course selected = courseListView.getSelectionModel().getSelectedItem();
      if (selected != null) {
        showCourseDetails(selected);
      }
    });

    Label detailsLabel = new Label("Course Details:");
    detailsLabel.setStyle("-fx-font-weight: bold;");

    detailsArea = new TextArea();
    detailsArea.setEditable(false);
    detailsArea.setPrefHeight(200);
    detailsArea.setPromptText("Select a course to view details");

    Button refreshButton = new Button("Refresh List");
    refreshButton.setOnAction(e -> loadCourses());

    getChildren().addAll(
        titleLabel,
        infoLabel,
        listLabel,
        courseListView,
        detailsLabel,
        detailsArea,
        refreshButton);
  }

  private void loadCourses() {
    List<Course> myCourses = courseService.getAllCourses().stream()
        .filter(c -> c.getSubjects().stream()
            .anyMatch(s -> s.getTeachers().stream()
                .anyMatch(t -> t.getId().equals(currentTeacher.getId()))))
        .toList();

    courseListView.setItems(FXCollections.observableArrayList(myCourses));
  }

  private void showCourseDetails(Course course) {
    StringBuilder details = new StringBuilder();
    details.append("ID: ").append(course.getId()).append("\n");
    details.append("Name: ").append(course.getName()).append("\n");

    if (course.getDescription() != null) {
      details.append("Description: ").append(course.getDescription()).append("\n");
    }

    if (course.getYear() != null) {
      details.append("Year: ").append(course.getYear()).append("\n");
    }

    if (course.getSemester() != null) {
      details.append("Semester: ").append(course.getSemester()).append("\n");
    }

    details.append("\nSubjects in this course:\n");
    course.getSubjects()
        .forEach(s -> details.append("  - ").append(s.getName()).append(" (").append(s.getLanguage()).append(")\n"));

    details.append("\nEnrolled Students: ").append(course.getStudents().size()).append("\n");
    course.getStudents().forEach(s -> details.append("  - ").append(s.getName()).append("\n"));

    detailsArea.setText(details.toString());
  }
}
