package com.vityarthi.cove.io;

import com.vityarthi.cove.model.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Character-Oriented Stream Exporter for Project Dossiers.
 * Demonstrates:
 * - CSE2006 Unit 4 & Indicative Experiment 16: Character Streams (Writer, BufferedWriter, PrintWriter).
 */
@Component
public class CharacterStreamReportExporter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Generates a complete project dossier formatted as text/markdown using Character Streams.
     */
    public String exportProjectDossier(Project project) throws IOException {
        StringWriter stringWriter = new StringWriter();
        try (BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
             PrintWriter writer = new PrintWriter(bufferedWriter)) {

            writer.println("================================================================================");
            writer.println("               ACADEMIC PROJECT DOSSIER & EVALUATION REPORT                     ");
            writer.println("               VIT Bhopal University - CSE2006 Java Platform                    ");
            writer.println("================================================================================");
            writer.println();
            writer.printf("Project Title    : %s%n", project.getTitle());
            writer.printf("Project ID       : %d%n", project.getId());
            writer.printf("Lifecycle Status : %s (%s)%n", project.getStatus().name(), project.getStatus().getDisplayLabel());
            writer.printf("Project Lead     : %s (%s, %s)%n",
                    project.getOwner().getName(),
                    project.getOwner().getRegistrationNumber(),
                    project.getOwner().getDepartment());
            writer.printf("Faculty Mentor   : %s%n",
                    project.getMentor() != null ? project.getMentor().getName() + " (" + project.getMentor().getDesignation() + ")" : "Pending Allocation");
            writer.printf("Deadline         : %s%n", project.getDeadline());
            writer.printf("Team Capacity    : %d / %d members%n",
                    project.getTeam() != null ? project.getTeam().getMembers().size() : 0, project.getTeamSizeLimit());
            writer.printf("Calculated Prog. : %.1f%%%n", project.calculateProgressPercentage());
            writer.printf("Report Generated : %s%n", LocalDateTime.now().format(DATE_FMT));
            writer.println();

            writer.println("--------------------------------------------------------------------------------");
            writer.println("1. PROJECT DESCRIPTION & TECH STACK");
            writer.println("--------------------------------------------------------------------------------");
            writer.println(project.getDescription());
            writer.printf("Technologies     : %s%n", project.getTechStack() != null ? project.getTechStack() : "Not specified");
            writer.println();

            writer.println("--------------------------------------------------------------------------------");
            writer.println("2. REQUIRED SKILLS SPECIFICATION");
            writer.println("--------------------------------------------------------------------------------");
            if (project.getRequiredSkills() == null || project.getRequiredSkills().isEmpty()) {
                writer.println("No mandatory skills defined.");
            } else {
                writer.printf("%-20s | %-15s | %-10s%n", "Skill Name", "Min Proficiency", "Weight");
                writer.println("---------------------+-----------------+-----------");
                for (ProjectSkill ps : project.getRequiredSkills()) {
                    writer.printf("%-20s | %-15s | %-10.1f%n",
                            ps.getSkill().getName(),
                            ps.getMinProficiency().name(),
                            ps.getWeight());
                }
            }
            writer.println();

            writer.println("--------------------------------------------------------------------------------");
            writer.println("3. TEAM ROSTER");
            writer.println("--------------------------------------------------------------------------------");
            if (project.getTeam() != null && !project.getTeam().getMembers().isEmpty()) {
                writer.printf("%-20s | %-15s | %-15s | %-16s%n", "Student Name", "Reg. Number", "Role in Team", "Joined Date");
                writer.println("---------------------+-----------------+-----------------+-----------------");
                for (TeamMember tm : project.getTeam().getMembers()) {
                    writer.printf("%-20s | %-15s | %-15s | %-16s%n",
                            tm.getStudent().getName(),
                            tm.getStudent().getRegistrationNumber(),
                            tm.getRoleInTeam(),
                            tm.getJoinedAt().format(DATE_FMT));
                }
            } else {
                writer.println("No team members currently registered.");
            }
            writer.println();

            writer.println("--------------------------------------------------------------------------------");
            writer.println("4. MILESTONES ROADMAP");
            writer.println("--------------------------------------------------------------------------------");
            if (project.getMilestones() != null && !project.getMilestones().isEmpty()) {
                writer.printf("%-30s | %-12s | %-8s | %-10s%n", "Milestone", "Deadline", "Weight", "Status");
                writer.println("-------------------------------+--------------+----------+-----------");
                for (Milestone m : project.getMilestones()) {
                    writer.printf("%-30s | %-12s | %-7.1f%% | %-10s%n",
                            m.getTitle(),
                            m.getTargetDeadline(),
                            m.getWeightPercentage(),
                            m.isCompleted() ? "COMPLETED" : "PENDING");
                }
            } else {
                writer.println("No milestones recorded yet.");
            }
            writer.println();

            writer.println("--------------------------------------------------------------------------------");
            writer.println("5. TASK MATRIX");
            writer.println("--------------------------------------------------------------------------------");
            if (project.getTasks() != null && !project.getTasks().isEmpty()) {
                writer.printf("%-30s | %-18s | %-10s | %-12s | %-12s%n", "Task Title", "Assignee", "Priority", "Status", "Deadline");
                writer.println("-------------------------------+--------------------+------------+--------------+-------------");
                for (Task t : project.getTasks()) {
                    writer.printf("%-30s | %-18s | %-10s | %-12s | %-12s%n",
                            t.getTitle(),
                            t.getAssignee() != null ? t.getAssignee().getName() : "Unassigned",
                            t.getPriority().name(),
                            t.getStatus().name(),
                            t.getDeadline());
                }
            } else {
                writer.println("No tasks currently added.");
            }
            writer.println();

            writer.println("================================================================================");
            writer.println("                            END OF DOSSIER                                      ");
            writer.println("================================================================================");
            writer.flush();
        }

        return stringWriter.toString();
    }
}
