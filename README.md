# Basic LTI Portlet

This is a portlet that implements the IMS Basic Learning Tools Interoperability specification and allows you to render any Basic LTI enabled application inside uPortal. Possibilities include Sakai tools, Peoplesoft components, tools from other LMS's, collaboration and learning tools, blogs, forums, wikis, the list is endless.

### Features
* Allows a seamless integration between uPortal and any Basic LTI enabled application.
* Uses Basic LTI and OAuth for a secure single-sign-on connection.
* Completely integrate many different external applications into uPortal within minutes, instead of having to write web services to get the data and a new UI for each one.
* Can be registered as a content type so that Administrators can configure multiple instances of the portlet to point to different sources or send different launch data.
* Supports the CONFIG portlet mode for rich configuration.
* Easily extended by way of 'adapters' - these essentially pre-process the launch data to cater for the specific needs of the various Basic LTI endpoints. Adapters for many Basic LTI endpoints are included, as well as a standard no-op adapter for generic endpoints. Adapters for more applications are simple to create as well.

For more information, including a configuration and installation guide, architecture overview, project roadmap and screenshots, see:
<https://wiki.jasig.org/display/PLT/Basic+LTI+Portlet>