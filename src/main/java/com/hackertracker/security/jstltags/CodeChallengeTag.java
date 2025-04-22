package com.hackertracker.security.jstltags;

import com.hackertracker.security.dao.UserProblemPriorityDAO;
import com.hackertracker.security.dto.ProblemDTO;
import com.hackertracker.security.dto.UserProblemPriorityDTO;
import com.hackertracker.security.user.User;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;
import com.hackertracker.security.dao.UserDAO;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class CodeChallengeTag extends SimpleTagSupport {

    private User user;

    private UserDAO userDao;
    private UserProblemPriorityDAO userProblemPriorityDao;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void doTag() throws JspException, IOException {

        if (userDao == null) {
            WebApplicationContext ctx = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(((PageContext) getJspContext()).getServletContext());

            userDao = ctx.getBean(UserDAO.class);
        }

        if(userProblemPriorityDao == null) {
            WebApplicationContext ctx = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(((PageContext) getJspContext()).getServletContext());

            userProblemPriorityDao = ctx.getBean(UserProblemPriorityDAO.class);
        }

        JspWriter out = getJspContext().getOut();

        String username = user.getUsername();

        User myUser = userDao.getUserByUserName(username);

        List<UserProblemPriorityDTO> priorities = userProblemPriorityDao.findByUserOrderByPriorityScoreDesc(myUser);

        if (priorities == null || priorities.isEmpty()) {
            out.write("<p>No problems found.</p>");
            return;
        }

        out.write("<div class='problems-container'>");

        for (UserProblemPriorityDTO priority : priorities) {
            if (priority.getProblemDto() == null) {
                continue; // Skip if there's no problem data
            }

            out.write("<div class='problem-card'>");
            out.write("<h3>" + priority.getProblemDto().getQuestionTitle() + "</h3>");
            out.write("<p>Difficulty: " + priority.getProblemDto().getDifficultyLevel() + "</p>");
            out.write("<p>Priority Score: " + priority.getPriorityScore() + "</p>");

            // Display topics if available
            if (priority.getProblemDto().getTopics() != null && !priority.getProblemDto().getTopics().isEmpty()) {
                out.write("<div class='topics'>");
                out.write("<p>Topics: ");
                for (int i = 0; i < priority.getProblemDto().getTopics().size(); i++) {
                    out.write(priority.getProblemDto().getTopics().get(i).getTopicName());
                    if (i < priority.getProblemDto().getTopics().size() - 1) {
                        out.write(", ");
                    }
                }
                out.write("</p>");
                out.write("</div>");
            }

            // Add a link to the problem
            out.write("<a href='" + priority.getProblemDto().getPageUrl() + "' target='_blank'>Solve Problem</a>");
            out.write("</div>");
        }

        out.write("</div>");
    }
}