package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private AccountDAO accountDAO;

    // new AccountService with new AccountDao
    public AccountService() {
        accountDAO = new AccountDAO();
    }

    /*
     * Function: Register an Account into the Account table
     * Use AccountDAO to persist account into the database
     * @param account an account object.
     * @return The persisted account if the persistence is successful.
     */
    public Account registerAccount(Account account) {
        return accountDAO.addAccount(account);
    }

    /*
     * Function: Login to account if credentials match in th database
     * Use AccountDAO to check database for credentials
     * @param account an account object.
     * @return The account that was found to login
     */
    public Account loginToAccount(Account account) {
        return accountDAO.loginToAccount(account);
    }
}
