package com.vityarthi.cove.io;

import com.vityarthi.cove.model.Task;
import com.vityarthi.cove.model.TeamMember;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Byte-Oriented Stream Exporter generating downloadable CSV documents.
 * Demonstrates:
 * - CSE2006 Unit 4 & Indicative Experiment 16: Byte streams (ByteArrayOutputStream, BufferedOutputStream)
 *   bridged with OutputStreamWriter.
 */
@Component
public class ByteStreamDataExporter {

    /**
     * Exports a list of tasks into CSV byte array using Byte and Buffered Streams.
     */
    public byte[] exportTasksToCsv(List<Task> tasks) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (BufferedOutputStream bufferedStream = new BufferedOutputStream(byteStream);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedStream, StandardCharsets.UTF_8)) {

            // CSV Header
            writer.write("Task ID,Title,Assignee Name,Assignee RegNo,Priority,Status,Deadline,Completed At\n");

            for (Task t : tasks) {
                String assigneeName = t.getAssignee() != null ? escapeCsv(t.getAssignee().getName()) : "Unassigned";
                String assigneeReg = t.getAssignee() != null ? escapeCsv(t.getAssignee().getRegistrationNumber()) : "N/A";
                String completed = t.getCompletedAt() != null ? t.getCompletedAt().toString() : "N/A";

                writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                        t.getId(),
                        escapeCsv(t.getTitle()),
                        assigneeName,
                        assigneeReg,
                        t.getPriority().name(),
                        t.getStatus().name(),
                        t.getDeadline(),
                        completed
                ));
            }
            writer.flush();
        }
        return byteStream.toByteArray();
    }

    /**
     * Exports team roster to CSV format using Byte Streams.
     */
    public byte[] exportTeamRosterToCsv(List<TeamMember> members) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (BufferedOutputStream bufferedStream = new BufferedOutputStream(byteStream);
             OutputStreamWriter writer = new OutputStreamWriter(bufferedStream, StandardCharsets.UTF_8)) {

            writer.write("Member ID,Student Name,Registration Number,Department,Year,Role In Team,Joined Timestamp\n");

            for (TeamMember m : members) {
                writer.write(String.format("%d,%s,%s,%s,%d,%s,%s\n",
                        m.getId(),
                        escapeCsv(m.getStudent().getName()),
                        escapeCsv(m.getStudent().getRegistrationNumber()),
                        escapeCsv(m.getStudent().getDepartment()),
                        m.getStudent().getYearOfStudy(),
                        escapeCsv(m.getRoleInTeam()),
                        m.getJoinedAt().toString()
                ));
            }
            writer.flush();
        }
        return byteStream.toByteArray();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
