<blockquote>
<!-- This really wants to be a surrounding table with the app name,
     description, and summary in it
 -->
<table bgcolor="ffffff" cellpadding="2">
  <tr>
    <th colspan="3" bgcolor="#99ccff">
        <ss>WOAD</ss> Application Information
    </th>
  </tr>
  <tr>
    <th bgcolor="silver"> parameter </th>
    <th bgcolor="silver"> value </th>
    <th bgcolor="silver"> command-line option </th>
  </tr>
  <tr>
    <th bgcolor="silver"> target Server </th>
    <td> <get name="SITE:targetServer">&nbsp;</get>
    </td>
    <td> <code>target=<em><get name="SITE:targetServer">[host:port]</get></em>
	 </code> 
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> target Prefix </th>
    <td>  <get name="SITE:targetPrefix">&nbsp;</get>
    </td>
    <td> <code>prefix=<em><get name="SITE:targetPrefix">/path</get></em>
	 </code>
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> source Offset </th>
    <td>  <get name="SITE:sourceOffset">&nbsp;</get>
    </td>
    <td> <code>offset=<em><get name="SITE:sourceOffset">/path</get></em>
	 </code>
    </td>
  </tr>
  <tr>
    <th bgcolor="silver"> Application root </th>
    <td> &SITE:virtual;
    </td>
    <td> <code>-vroot <em>&SITE:virtual;</em></code> </td>
  </tr>
  <tr>
    <th bgcolor="silver"> Annotation root </th>
    <td>  &SITE:file;
    </td>
    <td> <code>-root <em>&SITE:file;</em></code> </td>
  </tr>
</table>
</blockquote>

<inc-footer cvsid="$Id: app-info.inc,v 1.1 2000-06-08 23:55:42 steve Exp $">
