package com.vityarthi.cove.repository;

import com.vityarthi.cove.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByTeamId(Long teamId);
    List<TeamMember> findByStudentId(Long studentId);
    Optional<TeamMember> findByTeamIdAndStudentId(Long teamId, Long studentId);
    boolean existsByTeamIdAndStudentId(Long teamId, Long studentId);
    void deleteByTeamIdAndStudentId(Long teamId, Long studentId);
}
