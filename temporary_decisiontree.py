
actions_train['count'] = actions_train.groupby('user_id').cumcount()
actions_train['change_ratio'] = (actions_train['break_val']-1)/actions_train['count']
jj = pd.merge(actions_train,profiles,on='user_id')
jj = jj[['action','previous_action','job_seeking_status','timediff','previous_streak','change_ratio','count','number_contacts']]
jj = jj.fillna(0)

from sklearn.preprocessing import OneHotEncoder
previous_action_encoder = OneHotEncoder(categories='auto')
job_seek_status_encoder =  OneHotEncoder(categories='auto')
prev_action_ohe = previous_action_encoder.fit_transform(jj.previous_action.values.astype('int').reshape(-1,1))
job_seek_status_ohe = job_seek_status_encoder.fit_transform(jj.job_seeking_status.values.astype('int').reshape(-1,1))


#rr = pd.concat([jj[['timediff','previous_streak','change_ratio','count','number_contacts']],
#          pd.DataFrame(prev_action_ohe.toarray(), columns = ['prev_0','prev_1','prev_2','prev_3','prev_4','prev_5']),
#          pd.DataFrame(job_seek_status_ohe.toarray(), columns = ['seek_1','seek_2','seek_3'])], axis=1 )
rr = pd.concat([jj[['timediff','previous_streak','change_ratio','count']],
          pd.DataFrame(prev_action_ohe.toarray(), columns = ['prev_0','prev_1','prev_2','prev_3','prev_4','prev_5'])], axis=1 )

from sklearn.tree import DecisionTreeClassifier  
classifier = DecisionTreeClassifier(criterion='entropy',max_depth=3)  
classifier.fit(rr, jj.action)  

from sklearn.externals.six import StringIO  
from IPython.display import Image  
from sklearn.tree import export_graphviz
import pydotplus

dot_data = open('test.dot','w')
export_graphviz(classifier, out_file=dot_data,  
                class_names=['1','2','3','4','5'],
                filled=True, rounded=True,
                special_characters=True)
dot_data.close()

actions_test = pd.read_csv('test.csv', sep='\t')
grouped = actions_test.groupby('user_id')
actions_test['previous_action'] = grouped['action'].shift(+1)
actions_test['previous_timestamp'] = grouped['timestamp'].shift(+1)
actions_test['timediff'] = actions_test['timestamp'] - actions_test['previous_timestamp'] 
actions_test['break_action'] = actions_test['previous_action'] != actions_test['action']
actions_test['repeated_action'] = actions_test['previous_action'] == actions_test['action']
actions_test['break_val'] = actions_test.groupby('user_id')['break_action'].cumsum()
actions_test['streak'] = actions_test.groupby(['user_id','break_val'])['repeated_action'].cumsum()+1
actions_test['previous_streak'] = actions_test.groupby('user_id')['streak'].shift(+1)
actions_test['count'] = actions_test.groupby('user_id').cumcount()
actions_test['change_ratio'] = (actions_test['break_val']-1)/actions_test['count']


tt = pd.merge(actions_test,profiles,on='user_id')
tt = tt[['action','previous_action','job_seeking_status','timediff','previous_streak','change_ratio','count','number_contacts']]
tt = tt.fillna(0)

previous_action_encoder = OneHotEncoder(categories='auto')
job_seek_status_encoder =  OneHotEncoder(categories='auto')
prev_action_ohe = previous_action_encoder.fit_transform(tt.previous_action.values.astype('int').reshape(-1,1))
job_seek_status_ohe = job_seek_status_encoder.fit_transform(tt.job_seeking_status.values.astype('int').reshape(-1,1))

#tt2 = pd.concat([tt[['timediff','previous_streak','change_ratio','count','number_contacts']],
#          pd.DataFrame(prev_action_ohe.toarray(), columns = ['prev_0','prev_1','prev_2','prev_3','prev_4','prev_5']),
#          pd.DataFrame(job_seek_status_ohe.toarray(), columns = ['seek_1','seek_2','seek_3'])], axis=1 )
tt2 = pd.concat([tt[['timediff','previous_streak','change_ratio','count']],
          pd.DataFrame(prev_action_ohe.toarray(), columns = ['prev_0','prev_1','prev_2','prev_3','prev_4','prev_5'])], axis=1 )

next_pred=classifier.predict(tt2)   

from sklearn import metrics
print ("Accuracy:{0:.3f}".format(metrics.accuracy_score(tt.action,next_pred)),"\n")
print ("Classification report")
print (metrics.classification_report(tt.action,next_pred),"\n")
print ("Confusion matrix")
print (metrics.confusion_matrix(tt.action,next_pred),"\n")

pd.Series(next_pred).hist()
