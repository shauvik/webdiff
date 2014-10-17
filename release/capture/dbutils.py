import MySQLdb, string
from random import choice

#Mysql functions

def dbConnect(conf):
        DB = conf['database']
        DB_HOST = conf['host']
        DB_PORT = int(conf['port'])
        DB_USER = conf['user']
        DB_PASSWORD = conf['password']
        conn = MySQLdb.Connection(db=DB, host=DB_HOST, port=DB_PORT, user=DB_USER, passwd=DB_PASSWORD)
        return conn
    
def getSessions(cursor):
        sql = """select sessid from sessions;"""
        cursor.execute(sql)
        results = cursor.fetchall()
        sessid=list()
        for row in results:
            sessid.append(row[0])
        return sessid

def getTests(cursor,sessid):
        sql = """select testid from tests where sessid='"""+sessid+"""';"""
        cursor.execute(sql)
        results = cursor.fetchall()
        testid=list()
        for row in results:
            testid.append(row[0])
        return testid

def genSessId():
    chars = string.letters + string.digits
    newpasswd=''
    for i in range(4):
        newpasswd = newpasswd + choice(chars)
    return newpasswd

def uniqueSessId(results):
        newSessId=''
        foundId=True
        while foundId==True:
            newSessId=genSessId()
            foundId=newSessId in results
        return newSessId
    
def insertTest(browsers,conn,cursor,sessid, url):
    for test in browsers:
        sql = """INSERT INTO tests(sessid,browser,user_agent) VALUES ('"""+sessid+"""','"""+test+"""','"""+test+"""');"""
        try:
           # Execute the SQL command
           cursor.execute(sql)
           # Commit your changes in the database
           conn.commit()
        except:
           # Rollback in case there is any error
           conn.rollback()

def insertSession(conn,cursor,sessid, url):
    sql = """INSERT INTO sessions(sessid,url) VALUES ('"""+sessid+"""','"""+url+"""');"""
    try:
       # Execute the SQL command
       cursor.execute(sql)
       # Commit your changes in the database
       conn.commit()
    except:
       # Rollback in case there is any error
       conn.rollback()

def markTest(testid,no,conn,cursor):
    sql = "UPDATE tests SET status=%d WHERE testid = %d" %(no,testid)
    try:
       # Execute the SQL command
       cursor.execute(sql)
       # Commit your changes in the database
       conn.commit()
    except:
       # Rollback in case there is any error
       conn.rollback()

def mySQLtest(url,browsers,config):
    conn=dbConnect(config["db_config"])
    cursor = conn.cursor()
    results=getSessions(cursor)
    sessid=uniqueSessId(results)
    insertSession(conn,cursor,sessid,url)
    insertTest(browsers,conn,cursor,sessid,url)
    tests=getTests(cursor,sessid)
    return cursor,conn,tests,sessid #set of testid

def resetAnalysis(conn):
    queries = ["TRUNCATE issues;","TRUNCATE map;","TRUNCATE report;","UPDATE `domdata` SET `non_det`=0 WHERE 1"]
    runQueries(conn, queries)
    
def cleardb(conn):
    queries = ["TRUNCATE `domdata`;", "TRUNCATE `issues`;", "TRUNCATE `map`;", "TRUNCATE `report`;", "TRUNCATE `requests`;", "TRUNCATE `sessions`;", "TRUNCATE `session_users`;", "TRUNCATE `tests`;", "TRUNCATE `users`;"]
    runQueries(conn, queries)
    
def runQueries(conn, queries):
    cursor = conn.cursor()
    for sql in queries:
        try:
           cursor.execute(sql)
           conn.commit()
        except:
           conn.rollback()
