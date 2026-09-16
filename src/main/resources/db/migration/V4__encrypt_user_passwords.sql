UPDATE usuarios
SET senha = '$2a$10$QvCC4GR/jsNnvUtHYr5cyOjH6aS0SC.XYJi/mz5rfqL6nrQ2iBXCK'
WHERE login = 'admin' AND senha = 'admin123';

UPDATE usuarios
SET senha = '$2a$10$0lD/iGb1.7BwZV2FNaIu/uGTxt4sWOalnVvz2hz.CEnU.dDajcl2i'
WHERE login = 'funcionario' AND senha = '123456';

UPDATE usuarios
SET senha = '$2a$10$FL7yYPdKrATVW9y6PgJNwuZwMhNvzOGcvMtiTygk66foi3C.eCxri'
WHERE login = 'joao' AND senha = '123456';

UPDATE usuarios
SET senha = '$2a$10$IyOtL0ysF0LnHv/K4su3Ee/r4soTLFP911yB4p0QhIaExAQf75A4.'
WHERE login = 'maria' AND senha = '123456';