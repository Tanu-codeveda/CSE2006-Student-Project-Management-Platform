# Rule-Based Skill Matching Algorithm Specification

## 1. Algorithmic Overview

The teammate matching engine is a **deterministic, rule-based compatibility scoring system** implemented in `SkillMatchingService.java`. It evaluates registered students against a project's required skills specifications.

> [!NOTE]
> This algorithm does **NOT** use black-box machine learning or artificial intelligence. It computes transparent set overlap, proficiency weight bonuses, and availability modifiers, ensuring every match is explainable.

---

## 2. Mathematical Formulation

Let $R = \{r_1, r_2, \dots, r_m\}$ be the set of required project skills, where each $r_i$ possesses:
- $\text{Name}(r_i)$: The unique skill name.
- $\text{ReqLevel}(r_i) \in \{1, 2, 3, 4\}$: Minimum required proficiency rank (`BEGINNER`=1, `INTERMEDIATE`=2, `ADVANCED`=3, `EXPERT`=4).
- $W(r_i) \in \mathbb{R}^+$: The importance weight assigned by the project lead (default 1.0).

Let $S = \{s_1, s_2, \dots, s_k\}$ be the candidate student's acquired skills, where each $s_j$ has $\text{Name}(s_j)$ and $\text{Level}(s_j) \in \{1, 2, 3, 4\}$.

### 2.1 Overlap Percentage
$$\text{Overlap \%} = \frac{|\{r \in R \mid \exists s \in S \text{ where } \text{Name}(r) = \text{Name}(s)\}|}{|R|} \times 100$$

### 2.2 Weighted Proficiency Score
For each required skill $r_i \in R$:
$$
\text{Score}(r_i) =
\begin{cases}
W(r_i) \times \left(1.0 + 0.1 \times (\text{Level}(s_j) - \text{ReqLevel}(r_i))\right) & \text{if } \text{Name}(s_j) = \text{Name}(r_i) \text{ and } \text{Level}(s_j) \ge \text{ReqLevel}(r_i) \\
W(r_i) \times 0.6 & \text{if } \text{Name}(s_j) = \text{Name}(r_i) \text{ and } \text{Level}(s_j) < \text{ReqLevel}(r_i) \\
0.0 & \text{if } r_i \notin S
\end{cases}
$$

The normalized weighted compatibility index is:
$$\text{Compatibility Score} = \frac{\sum_{i=1}^m \text{Score}(r_i)}{\sum_{i=1}^m W(r_i)}$$

---

## 3. Compatibility Tiers

| Threshold | Compatibility Tier | Description |
| :--- | :--- | :--- |
| Overlap = 100% and Score $\ge 1.0$ | `PERFECT_MATCH` | Candidate possesses all required skills at or above target proficiency. |
| Overlap $\ge 70\%$ | `STRONG_MATCH` | High skill alignment; well-suited for primary technical deliverables. |
| $40\% \le \text{Overlap} < 70\%$ | `PARTIAL_MATCH` | Moderate alignment; candidate satisfies subset of required competencies. |
| Overlap $< 40\%$ | `LOW_MATCH` | Significant skill gaps relative to project requirements. |

---

## 4. Concrete Example

### Project Requirements:
1. **Java**: Min Level: `INTERMEDIATE` ($=2$), Weight: $1.5$
2. **SQL**: Min Level: `INTERMEDIATE` ($=2$), Weight: $1.2$
3. **React**: Min Level: `BEGINNER` ($=1$), Weight: $1.0$
- Total Max Weight: $1.5 + 1.2 + 1.0 = 3.7$

### Candidate Evaluation:

#### Student A (Rahul):
- **Java** (`EXPERT` $=4$): Meets requirement ($4 \ge 2$). $\text{Score} = 1.5 \times (1 + 0.1 \times (4 - 2)) = 1.5 \times 1.2 = 1.80$ $\rightarrow$ `MATCH ✓`
- **SQL** (`ADVANCED` $=3$): Meets requirement ($3 \ge 2$). $\text{Score} = 1.2 \times (1 + 0.1 \times (3 - 2)) = 1.2 \times 1.1 = 1.32$ $\rightarrow$ `MATCH ✓`
- **React**: Not possessed. $\text{Score} = 0.0$ $\rightarrow$ `MISSING ✗`
- **Overlap**: $2 / 3 = 66.7\%$
- **Total Weighted Score**: $(1.80 + 1.32) / 3.7 = 3.12 / 3.7 = 0.843$ ($84.3\%$)
- **Tier**: `STRONG_MATCH`

#### Student B (Priya):
- **Java**: Not possessed $\rightarrow$ `MISSING ✗`
- **SQL**: Not possessed $\rightarrow$ `MISSING ✗`
- **React** (`ADVANCED` $=3$): Meets requirement ($3 \ge 1$). $\text{Score} = 1.0 \times (1 + 0.1 \times 2) = 1.20$ $\rightarrow$ `MATCH ✓`
- **Overlap**: $1 / 3 = 33.3\%$
- **Total Weighted Score**: $1.20 / 3.7 = 0.324$ ($32.4\%$)
- **Tier**: `LOW_MATCH`

---

## 5. Algorithmic Complexity

- **Time Complexity**: $\mathcal{O}(N \times M)$, where $N$ is the number of active students and $M$ is the number of required project skills. Since students have an average of $K \approx 5$ skills, lookup is effectively $\mathcal{O}(1)$ via HashSet. For $N = 1000$ students, evaluation executes in under **10 milliseconds**.
- **Space Complexity**: $\mathcal{O}(N)$ to store candidate score wrappers for sorting.
