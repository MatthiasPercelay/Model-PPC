library(XLConnect)

xlsfile <- "ucl-planning-december-19.xls"
wb <- loadWorkbook(xlsfile, create = FALSE)

ToTab <- function(x) paste( '[', paste(x, collapse = ', '), ']')
ToTab2D <- function(x) paste( '[', paste(x, collapse = ',\n'), ']', sep = '\n')  

df <- readNamedRegion(wb, 'planning', header = FALSE, colTypes = XLC$DATA_TYPE.STRING)

datfile <- gsub('.xlsx?$', '.dat', xlsfile)
sink(datfile)
cat(sprintf("n=%d;\nc=%d;\n", nrow(df), ncol(df)/(7*2)))

planning <- apply(df, 1, sprintf, fmt = '\"%s\"')
planning <- apply(planning, 2, ToTab)
cat('planning= ', ToTab2D(planning) , ';\n', sep = '')

x <- readNamedRegion(wb, 'workDays', header = FALSE, colTypes = XLC$DATA_TYPE.NUMERIC, simplify = TRUE)
cat('workDays= ', ToTab(x), ';\n', sep = '')

x <- readNamedRegion(wb, 'breaksPerCycle', header = FALSE, colTypes = XLC$DATA_TYPE.NUMERIC, simplify = TRUE)
cat('breaksPerCycle= ', ToTab(x), ';\n', sep = '')

df <- readNamedRegion(wb, 'demands', header = FALSE, colTypes = XLC$DATA_TYPE.NUMERIC)
cat('demands= ', ToTab2D( apply(df, 1, ToTab)), ';\n', sep = '')

df <- readNamedRegion(wb, 'breakPrefs', header = FALSE, colTypes = XLC$DATA_TYPE.NUMERIC)
cat('breakPrefs= ', ToTab2D( apply(df, 1, ToTab)), ';\n', sep = '')
sink()
