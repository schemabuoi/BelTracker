/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beltracker.dal;

import beltracker.be.Department;
import beltracker.be.Order;
import beltracker.be.Task;
import beltracker.dal.dao.DepartmentDAO;
import beltracker.dal.dao.EventLogDAO;
import beltracker.dal.dao.OrderDAO;
import beltracker.dal.dao.TaskDAO;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Acer
 */
public class DALManager implements IDALFacade{
    
    private static final String DB_PROPERTIES_FILE_PATH = "src/resources/properties/DatabaseProperties.properties";
    private DbConnectionProvider connector;
    private TaskDAO taskDao;
    private OrderDAO orderDao;
    private DepartmentDAO departmentDao;
    private EventLogDAO logDao;
    
    public DALManager()
    {
        try
        {
            connector = new DbConnectionProvider(DB_PROPERTIES_FILE_PATH);
            taskDao = new TaskDAO();
            orderDao = new OrderDAO();
            departmentDao = new DepartmentDAO();
            logDao = new EventLogDAO();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
            //TO DO
        }
    }

    @Override
    public List<Task> getTasks(Department department, LocalDate currentDate) 
    {
        Connection con = null;
        try
        {
            con = connector.getConnection();
            List<Task> tasks = taskDao.getTasks(con, department, currentDate);
            for(Task departmentTask : tasks)
            {
                Order taskOrder = orderDao.getOrder(con, departmentTask);
                departmentTask.setOrder(taskOrder);
            }
            return tasks;
        } 
        catch(SQLServerException ex) 
        {
            ex.printStackTrace();
            return null;
            //TO DO
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return null;
            //TO DO
        }
        finally
        {
            if(con != null)
            {
                connector.releaseConnection(con);
            }
        }
    }

    @Override
    public List<Department> getAllDepartments() 
    {
        Connection con = null;
        try
        {
            con = connector.getConnection();
            return departmentDao.getAllDepartments(con);
        } 
        catch(SQLServerException ex) 
        {
            ex.printStackTrace();
            return null;
            //TO DO
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            return null;
            //TO DO
        }
        finally
        {
            if(con != null)
            {
                connector.releaseConnection(con);
            }
        }
    }

    @Override
    public void submitTask(Task task, long currentEpochTime) {
        Connection con = null;
        try
        {
            con = connector.getConnection();
            taskDao.submitTask(con, task);
            logDao.logEvent(con, task, currentEpochTime, EventLogDAO.EventType.SUBMIT_TASK);
        } 
        catch(SQLServerException ex) 
        {
            ex.printStackTrace();
            //TO DO
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
            //TO DO
        }
        finally
        {
            if(con != null)
            {
                connector.releaseConnection(con);
            }
        }
    }
    
}
